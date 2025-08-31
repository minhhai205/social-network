package com.minhhai.social_network.config;

import com.minhhai.social_network.config.security.securityCustom.CustomJwtDecoder;
import com.minhhai.social_network.exception.AppException;
import com.minhhai.social_network.repository.ConversationMemberRepository;
import com.minhhai.social_network.repository.UserRepository;
import com.minhhai.social_network.util.enums.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final CustomJwtDecoder jwtDecoder;
    private final UserRepository userRepository;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    private final ConversationMemberRepository conversationMemberRepository;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS()
                .setHeartbeatTime(25000)
                .setClientLibraryUrl("https://cdn.jsdelivr.net/npm/sockjs-client@1.6.1/dist/sockjs.min.js");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes("/app");

        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{25000, 25000})
                .setTaskScheduler(myMessageBrokerTaskScheduler());

        registry.setUserDestinationPrefix("/users");
    }

    @Bean
    public ThreadPoolTaskScheduler myMessageBrokerTaskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(1);
        scheduler.setThreadNamePrefix("wss-heartbeat-");
        scheduler.initialize();
        return scheduler;
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration registry) {
        registry.setMessageSizeLimit(1024 * 1024 * 3); // 3MB
        registry.setSendBufferSizeLimit(1024 * 1024 * 3);
        registry.setSendTimeLimit(20 * 1000);
        registry.setTimeToFirstMessage(30 * 1000);
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4);
        registration.taskExecutor().maxPoolSize(8);
        registration.taskExecutor().keepAliveSeconds(60);
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (accessor == null) return message;

                StompCommand command = accessor.getCommand();
                if (command == null) return message;

               try{
                   switch (command) {
                       case CONNECT -> handleConnect(accessor);
                       case SUBSCRIBE -> handleSubscribe(accessor);
                       case SEND -> handleSend(accessor);
                       case DISCONNECT -> handleDisconnect(accessor);
                   }
               } catch (Exception e) {
                   log.error("------ STOMP unexpected error: {}", e.getMessage());

                   StompHeaderAccessor errorAccessor = StompHeaderAccessor.create(StompCommand.ERROR);
                   errorAccessor.setMessage(e.getMessage());
                   errorAccessor.setLeaveMutable(true);

                   return MessageBuilder.createMessage(
                           e.getMessage().getBytes(StandardCharsets.UTF_8),
                           errorAccessor.getMessageHeaders()
                   );
               }

                return message;
            }

            @Override
            public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
                if (ex != null) {
                    log.error("----------- Error sending message: ", ex);
                }
            }
        });

        registration.taskExecutor().corePoolSize(4);
        registration.taskExecutor().maxPoolSize(8);
        registration.taskExecutor().keepAliveSeconds(60);
    }

    private void handleConnect(StompHeaderAccessor accessor){
            String jwtToken = extractTokenFromHeader(
                    Objects.requireNonNull(accessor.getNativeHeader("Authorization")).get(0));

            Jwt jwt = jwtDecoder.decode(jwtToken);
            Authentication authentication = jwtAuthenticationConverter.convert(jwt);
            accessor.setUser(authentication);

            // Có thể xác định được user sau khi disconnect
            accessor.getSessionAttributes().put("connectTime", System.currentTimeMillis());
            accessor.getSessionAttributes().put("username", authentication.getName());

            setIsOnlineUser(authentication.getName(), true);
            log.info("------------ User {} connected to WebSocket ---------------", authentication.getName());
    }

    private void handleSubscribe(StompHeaderAccessor accessor){
        String destination = accessor.getDestination();
        if (destination == null) {
            throw new AppException(ErrorCode.DESTINATION_INVALID);
        }
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        checkConversationAccess(username, destination);
        log.info("-------------- User {} subscribed to {} ----------------", username, destination);
    }

    private void handleSend(StompHeaderAccessor accessor){
//        String username = accessor.getUser().getName();
//        userRepository.findByUsername(username).orElseThrow(
//                () -> new AppException(ErrorCode.ACCESS_DENIED));
    }

    private void handleDisconnect(StompHeaderAccessor accessor){
        String username = Objects.requireNonNull(accessor.getUser()).getName();
        setIsOnlineUser(username, false);
        log.info("--------------- User {} disconnected from WebSocket ---------------", username);
    }

    private String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        } else {
            throw new AppException(ErrorCode.ACCESS_TOKEN_INVALID);
        }
    }

    private void setIsOnlineUser(String username , boolean status) {
        // Thường làm xét trạng thái user onl hay off trong redis để nhận biết
        // Có các event để thông báo tới bạn bè mình đang onl hay off
        // Phần này luồng logic cần suy nghĩ cẩn thận tùy logic áp dụng
    }

    private void checkConversationAccess(String username, String destination) {
        if (destination.contains("conversation")) {
            Long conversationId = Long.parseLong(destination.substring(destination.lastIndexOf("/") + 1));

            conversationMemberRepository.findMemberByUsername(username, conversationId)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCESS_DENIED));
        }
    }
}