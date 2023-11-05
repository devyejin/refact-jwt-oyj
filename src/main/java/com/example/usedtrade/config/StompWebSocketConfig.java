package com.example.usedtrade.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;
import org.springframework.security.config.annotation.web.socket.EnableWebSocketSecurity;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //STOMP 메시지브로커 사용하기위해 선언
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // stomp 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/stomp/chat") //클라이언트가 웹 소켓 연결 시도할 때 /stomp/chat 경로를 사용
                .setAllowedOrigins("http://localhost:8080") //웹 소켓 연결을 허용할 오리진(Origin)을 지정
                .withSockJS(); //SockJS라이브러리 활성화

    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
            registry.setApplicationDestinationPrefixes("/pub"); // Client에서 SEND요청 처리 (발행)
            registry.enableSimpleBroker("/sub"); // '/sub' 경로를 구독(SUBSCRIBE)하는 Cleint에게 메시지 전달하는 브로커
        }
}
