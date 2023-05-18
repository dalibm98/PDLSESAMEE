package com.PDL.Sesame.configmessage;

import com.PDL.Sesame.model.Message;
import com.PDL.Sesame.model.MessageDto;
import com.PDL.Sesame.model.User;
import com.PDL.Sesame.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {

    @Autowired
    private MessageService messageService;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/ws").setAllowedOrigins("*");
    }

    public class WebSocketHandler extends TextWebSocketHandler {
        private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

        @Override
        public void afterConnectionEstablished(WebSocketSession session) {
            String username = session.getPrincipal().getName();
            sessions.put(username, session);
        }

        @Override
        public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
            String username = session.getPrincipal().getName();
            sessions.remove(username);
        }

        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            MessageDto messageDto = mapper.readValue(message.getPayload(), MessageDto.class);
            User sender = messageService.getUserByEmail(messageDto.getSenderUsername());
            User recipient = messageService.getUserByEmail(messageDto.getRecipientUsername());
            Message newMessage = messageService.sendMessage(sender, recipient, messageDto.getContent());
            List<WebSocketSession> recipientSessions = getRecipientSessions(recipient.getUsername());
            for (WebSocketSession recipientSession : recipientSessions) {
                recipientSession.sendMessage(new TextMessage(mapper.writeValueAsString(new MessageDto(newMessage))));
            }
        }

        private List<WebSocketSession> getRecipientSessions(String recipientUsername) {
            List<WebSocketSession> recipientSessions = new ArrayList<>();
            for (WebSocketSession session : sessions.values()) {
                if (session.getPrincipal().getName().equals(recipientUsername)) {
                    recipientSessions.add(session);
                }
            }
            return recipientSessions;
        }
    }
}
