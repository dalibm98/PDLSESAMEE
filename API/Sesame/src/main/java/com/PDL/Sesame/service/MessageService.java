package com.PDL.Sesame.service;

import com.PDL.Sesame.dao.MessageRepository;
import com.PDL.Sesame.model.Message;
import com.PDL.Sesame.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(User sender, User recipient, String content) {
        Message message = new Message(sender, recipient, content);
        return messageRepository.save(message);
    }

    public List<Message> getMessages(User user) {
        return messageRepository.findBySenderOrRecipientOrderByCreatedAtDesc(user, user);
    }

    // Nouvelle méthode pour récupérer les messages entre deux utilisateurs spécifiques
    public List<Message> getMessagesBetweenUsers(User user1, User user2) {
        return messageRepository.findBySenderAndRecipientOrSenderAndRecipientOrderByCreatedAtDesc(user1, user2, user2, user1);
    }
}