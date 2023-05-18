package com.PDL.Sesame.controleur;

import com.PDL.Sesame.auth.AuthenticationService;
import com.PDL.Sesame.model.Message;
import com.PDL.Sesame.model.MessageDto;
import com.PDL.Sesame.model.User;
import com.PDL.Sesame.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@Tag(name = "Messages", description = "Operations related to Messages")
public class MessageController {
    private final MessageService messageService;
    private final AuthenticationService userService;

    public MessageController(MessageService messageService, AuthenticationService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @Operation(summary = "Send Message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})

    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        User sender = userService.getCurrentUser();
        User recipient = userService.getUserById(messageDto.getRecipientId());
        Message message = messageService.sendMessage(sender, recipient, messageDto.getContent());
        MessageDto messageDtoResponse = new MessageDto(message);

        messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/topic/messages", messageDtoResponse);

        return messageDtoResponse;
    }

    @MessageMapping("/messages")
    public void receiveMessage(@Payload MessageDto messageDto) {
        User sender = userService.getCurrentUser();
        User recipient = userService.getUserById(messageDto.getRecipientId());
        Message message = messageService.sendMessage(sender, recipient, messageDto.getContent());
        MessageDto messageDtoResponse = new MessageDto(message);

        messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/topic/messages", messageDtoResponse);
    }

    @GetMapping
    @Operation(summary = "Get all Messages")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Messages avec user connectée"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public List<MessageDto> getMessages() {
        User user = userService.getCurrentUser();
        List<Message> messages = messageService.getMessages(user);
        return messages.stream().map(MessageDto::new).collect(Collectors.toList());
    }

    @GetMapping("/messages/{recipientUsername}")
    @Operation(summary = "Get all Messages with specific recipient")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "all Messages with specific recipient"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public List<MessageDto> getMessagesWithRecipient(@PathVariable("recipientUsername")Integer idRecipient) {
        User currentUser = userService.getCurrentUser();
        User recipientUser = userService.getUserById(idRecipient.intValue());
        List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, recipientUser);
        return messages.stream().map(MessageDto::new).collect(Collectors.toList());
    }


}