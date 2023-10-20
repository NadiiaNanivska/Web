package com.example.server.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final YourWebSocketHandler yourWebSocketHandler;

    public WebSocketConfig(YourWebSocketHandler yourWebSocketHandler) {
        this.yourWebSocketHandler = yourWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(yourWebSocketHandler, "/ws-endpoint")
                .setAllowedOrigins("*");
    }
}
