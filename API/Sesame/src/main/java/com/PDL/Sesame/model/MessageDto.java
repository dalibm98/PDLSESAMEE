package com.PDL.Sesame.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String senderUsername;
    private Integer recipientId; // change to Integer
    private String recipientUsername;
    private String content;
    private LocalDateTime createdAt;

    public MessageDto(Message message) {
        this.id = message.getId();
        this.senderUsername = message.getSender().getUsername();
        this.recipientId = message.getRecipient().getId().intValue(); // convert Long to Integer
        this.recipientUsername = message.getRecipient().getUsername();
        this.content = message.getContent();
        this.createdAt = message.getCreatedAt();
    }

    public Integer getRecipientId() { // change return type to Integer
        return recipientId;
    }
}