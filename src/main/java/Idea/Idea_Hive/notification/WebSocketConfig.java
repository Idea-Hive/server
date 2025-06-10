package Idea.Idea_Hive.notification;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.setApplicationDestinationPrefixes("/app"); //메시지 발행 경로 (클라이언트 -> 서버로 메시지 보낼 때 사용)
        config.enableSimpleBroker("/topic"); //구독 경로 (서버 -> 클라이언트로 보낼 때 사용)
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") //초기 핸드셰이크 endPoint
                .setAllowedOrigins("*")
                .withSockJS();
    }
}
