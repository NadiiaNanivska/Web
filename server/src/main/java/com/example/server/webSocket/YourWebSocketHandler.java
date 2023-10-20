package com.example.server.webSocket;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@AllArgsConstructor
public class YourWebSocketHandler extends TextWebSocketHandler {
    private final Set<WebSocketSession> sessions = new CopyOnWriteArraySet<>();

    private TaskSessionManager taskSessionManager;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        taskSessionManager.associateTaskWithSession(session.getUri().getQuery().substring(7), session);
        sessions.add(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
//        taskSessionManager.removeTaskAssociation(session.getUri().getQuery().substring(7));
        sessions.remove(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String request = message.getPayload();
        String response = String.format("response from server to '%s'", HtmlUtils.htmlEscape(request));
        session.sendMessage(new TextMessage(response));
    }
    public void sendProgressUpdate(String uniqueId, String progress) throws IOException {
        TextMessage message = new TextMessage(progress);
        WebSocketSession currSession = taskSessionManager.getSessionForTask(uniqueId);
            if (currSession.isOpen()) {
                currSession.sendMessage(message);
            }
    }
}
