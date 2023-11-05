package com.example.usedtrade.domain.chat.dto;

import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Service
public class ChatRoomDTO {

    private String roomId;
    private String name;
    //WebSocketSession은  Spring Framework에서 제공하는 클래스
    //웹 소켓 연결을 통해 클라이언트와 서버 간에 양방향 통신을 가능하게 하는 세션 객체 (세션ID,연결상태,연결끊기 등 가능)
    private Set<WebSocketSession> sessions = new HashSet<>();

    public static ChatRoomDTO create(String name) {
        ChatRoomDTO room = new ChatRoomDTO();

        room.roomId = UUID.randomUUID().toString();
        room.name = name;
        return room;

    }
}

