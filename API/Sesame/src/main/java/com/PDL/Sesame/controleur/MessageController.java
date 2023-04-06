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

    @PostMapping
    @Operation(summary = "Send Message")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message created"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})

    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        User sender = userService.getCurrentUser();
        User recipient = userService.getUserById(messageDto.getRecipientId());
        Message message = messageService.sendMessage(sender, recipient, messageDto.getContent());
        return new MessageDto(message);
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


    // Ajout de la fonctionnalité pour récupérer les messages entre deux utilisateurs spécifiques
    @GetMapping("/{recipientUsername}")
    @Operation(summary = "Get all Messages entre deux user spécifiques")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "all Messages entre deux user spécifiques"),
            @ApiResponse(responseCode = "401", description = "Unauthorized")})
    public List<MessageDto> getMessagesWithRecipient(@PathVariable String recipientUsername) {
        User currentUser = userService.getCurrentUser();
        User recipientUser = userService.getUserByEmail(recipientUsername);

        List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, recipientUser);
        return messages.stream().map(MessageDto::new).collect(Collectors.toList());
    }

}