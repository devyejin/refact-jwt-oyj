package com.example.usedtrade.domain.chat.controller;


import com.example.usedtrade.domain.chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class StompChatController {

    private final SimpMessagingTemplate template; // 특정 Broker로 메시지 전달

    //client가 SEND(발행) 할 수 있는 경로
    //config의 setApplicationDestinationPrefixes("/pub"); 의 prefix @MessageMapping value 경로가 병합됨
    //즉, /pub/chat/enter <-- SEND의 destination 헤더 값이 됨
    @MessageMapping(value = "/chat/enter") // <-- 이 어노테이션에서 SimpleAnnotationMethodMessageHadnler가 동작?
    public void enter(ChatMessageDTO message) {
        message.setMessage(message.getWriter() + "님이 채팅방에 참여하였습니다.");
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }
    //클라이언트가 /pub/chat/enter로 발행을 요청하면 컨트롤러가 해당 메시지를 받아서 처리, 값에 부연값(입장어쩌구저쩌구)하고
    //메시지가 발행되면 /sub/chat/room/[roomId] 로 메시지가 전송됨 (Broker 통해서)
    //구독하는 클라이언트는 /sub을 구독하다가 저 메시지 발행되면 받나봄?



    // pub/chat/message 로 발행될 때 처리
    @MessageMapping(value="/chat/message")
    public void message(ChatMessageDTO message) {
        template.convertAndSend("/sub/chat/room"+ message.getRoomId(), message);
    }


}


