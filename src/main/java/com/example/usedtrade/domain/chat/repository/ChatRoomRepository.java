package com.example.usedtrade.domain.chat.repository;

import com.example.usedtrade.domain.chat.dto.ChatRoomDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ChatRoomRepository {

    private Map<String, ChatRoomDTO> chatRoomDTOMap; //Map에다가 chatRoomDTO들을 저장할거야 (임시 저장소)

    @PostConstruct // 생성자 호출 이후 실행되는 메서드, 초기화 작업 수행할 때 활용, 메서드명 :자유, 반환타입 Void, 매개변수 x
    private void init() {
        chatRoomDTOMap = new LinkedHashMap<>();
    }

    public List<ChatRoomDTO> findAllRooms() {
        //채팅방이 최근에 생성된 순으로 반환
        List<ChatRoomDTO> result = new ArrayList<>(chatRoomDTOMap.values());
        Collections.reverse(result); //ollections.reverse() : 주어진 컬렉션 result를 역순으로 뒤집음

        return result;
    }

    public ChatRoomDTO findRoomById(String id) {
        return chatRoomDTOMap.get(id);
    }

    public ChatRoomDTO createChatRoomDTO(String name) {
        ChatRoomDTO room = ChatRoomDTO.create(name); //챗방 만들고
        chatRoomDTOMap.put(room.getRoomId(), room); //서버에 저장(추후 DB로)

        return room;
    }
}
