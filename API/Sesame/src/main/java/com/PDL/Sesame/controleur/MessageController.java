package com.PDL.Sesame.controleur;

import com.PDL.Sesame.auth.AuthenticationService;
import com.PDL.Sesame.model.Message;
import com.PDL.Sesame.model.MessageDto;
import com.PDL.Sesame.model.User;
import com.PDL.Sesame.service.MessageService;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageService messageService;
    private final AuthenticationService userService;

    public MessageController(MessageService messageService, AuthenticationService userService) {
        this.messageService = messageService;
        this.userService = userService;
    }

    @PostMapping
    public MessageDto sendMessage(@RequestBody MessageDto messageDto) {
        User sender = userService.getCurrentUser();
        User recipient = userService.getUserById(messageDto.getRecipientId());
        Message message = messageService.sendMessage(sender, recipient, messageDto.getContent());
        return new MessageDto(message);
    }


    @GetMapping
    public List<MessageDto> getMessages() {
        User user = userService.getCurrentUser();
        List<Message> messages = messageService.getMessages(user);
        return messages.stream().map(MessageDto::new).collect(Collectors.toList());
    }


    // Ajout de la fonctionnalité pour récupérer les messages entre deux utilisateurs spécifiques
    @GetMapping("/{recipientUsername}")
    public List<MessageDto> getMessagesWithRecipient(@PathVariable String recipientUsername) {
        User currentUser = userService.getCurrentUser();
        User recipientUser = userService.getUserByEmail(recipientUsername);

        List<Message> messages = messageService.getMessagesBetweenUsers(currentUser, recipientUser);
        return messages.stream().map(MessageDto::new).collect(Collectors.toList());
    }

}