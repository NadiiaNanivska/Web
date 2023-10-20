package com.example.server.webSocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TaskSessionManager {
    private final Map<String, WebSocketSession> taskSessionMap = new ConcurrentHashMap<>();

    public void associateTaskWithSession(String taskId, WebSocketSession session) {
        taskSessionMap.put(taskId, session);
    }

    public WebSocketSession getSessionForTask(String taskId) {
        return taskSessionMap.get(taskId);
    }

    public void removeTaskAssociation(String taskId) {
        taskSessionMap.remove(taskId);
    }
}
