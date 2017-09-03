package com.baldrick.texas.holdem.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

    public WebSocketConfig() {
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //The endpoint “/texasholdem” is registered for starting the WebSocket protocol handshake.
        registry.addEndpoint("/texasholdem").setAllowedOrigins("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        //messages whose destinations start with "/topic" will be routed to the message broker (i.e. broadcasting to other connected clients):
        //messages sent to topic/* will go to the broker
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost("localhost")
                .setRelayPort(61613);

        //messages whose destination starts with "/app" are routed to message-handling methods
        //messages sent to app/texasholdem will go to the controller
        registry.setApplicationDestinationPrefixes("/app");
    }
}