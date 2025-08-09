package com.minhhai.social_network.exception;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.security.Principal;

@ControllerAdvice
@RequiredArgsConstructor
public class MessageExceptionHandlerAdvice {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageExceptionHandler({AppException.class})
    public void handleIllegalMsgReceiver(AppException ex, Principal usr) {
        // when has error with socket api-send private message to /queue/error
        simpMessagingTemplate.convertAndSendToUser(usr.getName(), "/queue/error", ex.getMessage());
    }
}
