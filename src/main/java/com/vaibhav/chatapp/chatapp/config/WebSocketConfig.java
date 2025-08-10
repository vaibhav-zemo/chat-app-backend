package com.vaibhav.chatapp.chatapp.config;

import com.vaibhav.chatapp.chatapp.util.AppProperties;
import com.vaibhav.chatapp.chatapp.websocket.PrincipalHandshakeHandler;
import com.vaibhav.chatapp.chatapp.websocket.WebSocketAuthInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final WebSocketAuthInterceptor webSocketAuthInterceptor;
    private final AppProperties props;

    public WebSocketConfig(WebSocketAuthInterceptor webSocketAuthInterceptor, AppProperties props) {
        this.webSocketAuthInterceptor = webSocketAuthInterceptor;
        this.props = props;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .setHandshakeHandler(new PrincipalHandshakeHandler())
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(props.getRelayHost())
                .setRelayPort(props.getRelayPort())
                .setClientLogin(props.getRelayLogin())
                .setClientPasscode(props.getRelayPasscode())
                .setSystemLogin(props.getRelayLogin())
                .setSystemPasscode(props.getRelayPasscode())
                .setVirtualHost(props.getRelayVirtualHost());

        registry.setApplicationDestinationPrefixes("/app");
        registry.setUserDestinationPrefix("/user");
    }
}


