package com.minhhai.social_network.controller.socket;

import com.minhhai.social_network.service.socket.SocketGroupService;
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
@RequiredArgsConstructor
@Slf4j
public class SocketGroupController {
    private final SocketGroupService socketGroupService;

    @MessageMapping("/group/{groupId}/join-request")
    public void joinGroupRequest(
            @DestinationVariable @Min(value = 1, message = "group id must be greater than 0") long groupId,
            SimpMessageHeaderAccessor accessor
    ) {
       socketGroupService.createJoinRequest(groupId, accessor);
    }


    @MessageMapping("/group/join-request/{requestId}/approve")
    public void acceptJoinGroupRequest(
            @DestinationVariable @Min(value = 1, message = "request id must be greater than 0") long requestId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketGroupService.acceptJoinGroupRequest(requestId, accessor);
    }

    @MessageMapping("/group/join-request/{requestId}/reject")
    public void rejectJoinGroupRequest(
            @DestinationVariable @Min(value = 1, message = "request id must be greater than 0") long requestId,
            SimpMessageHeaderAccessor accessor
    ) {
        socketGroupService.rejectJoinGroupRequest(requestId, accessor);
    }
}
