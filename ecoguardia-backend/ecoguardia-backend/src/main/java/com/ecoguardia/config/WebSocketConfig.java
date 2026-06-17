package com.ecoguardia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configura el canal WebSocket para enviar mediciones en vivo al dashboard.
 *
 * El frontend se conecta a "ws://localhost:8080/ws" y se suscribe al
 * topic "/topic/mediciones" para recibir cada nueva medicion al instante.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Los clientes se suscriben a destinos que empiezan con /topic
        config.enableSimpleBroker("/topic");
        // Prefijo para mensajes que van del cliente al servidor (no lo usamos aun)
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Punto de conexion. SockJS da compatibilidad si el navegador no soporta WS puro.
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
