package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.dto.request.GroupChatAddUserRequestDTO;
import com.minhhai.social_network.dto.request.CreateConversationRequestDTO;
import com.minhhai.social_network.dto.request.GroupChatDeleteUserRequestDTO;
import com.minhhai.social_network.dto.request.MessageRequestDTO;
import com.minhhai.social_network.service.socket.SocketConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Tag(name = "Socket conversation controller")
@RequiredArgsConstructor
@Slf4j
public class SocketConversationController {
    private final SocketConversationService socketConversationService;

    @MessageMapping("/conversation/message/send_to/{conversationId}")
    public void sendMessage(
            @DestinationVariable @Min(value = 1, message = "User id must be greater than 0") long conversationId,
            @Valid MessageRequestDTO request,
            SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.sendMessage(request, conversationId, accessor);
    }


    @MessageMapping("/conversation/create")
    public void createConversation(
            @Valid CreateConversationRequestDTO createDTO, SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.createConversation(createDTO, accessor);
    }

    @MessageMapping("/conversation/group/add_member")
    public void addMemberToGroup(
            @Valid GroupChatAddUserRequestDTO addUserRequest, SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.addUserToGroupChat(addUserRequest, accessor);
    }

    @MessageMapping("/conversation/group/delete_member")
    public void deleteMemberFromGroup(
            @Valid GroupChatDeleteUserRequestDTO deleteUserRequest, SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.deleteMemberFromGroup(deleteUserRequest, accessor);
    }

    @MessageMapping("/conversation/group/{id}/leave_group")
    public void leaveGroup(
            @DestinationVariable @Min(value = 1, message = "Group id must be greater than 0") long id,
            SimpMessageHeaderAccessor accessor
    ){
        socketConversationService.leaveGroup(id, accessor);
    }
}
