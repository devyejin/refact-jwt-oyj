package com.example.usedtrade.domain.chat.controller;

import com.example.usedtrade.domain.chat.dto.ChatRoomDTO;
import com.example.usedtrade.domain.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping(value="/chat")
@Log4j2
public class RoomController { // 채팅화면 보여주는 용도

    private final ChatRoomRepository repository;

    //채팅방 목록 조회
    @GetMapping(value = "/rooms")
    public ModelAndView rooms() {
        log.info("채팅방 목록 보여줘 요청");
        ModelAndView mv = new ModelAndView("chat/rooms");
        mv.addObject("list", repository.findAllRooms());

        return mv;
    }

    //채팅방 조회
    @GetMapping("/room")
    public void getRoom(String roomId, Model model) {
        log.info("특정 방 조회, roomId= {}", roomId);
        model.addAttribute("room", repository.findRoomById(roomId));
    }

    @PostMapping("/create")
    public String createChatRoom(@RequestParam("name") String name, RedirectAttributes redirectAttributes) {
        log.info("새로운 채팅 방 생성 ");
        ChatRoomDTO chatRoomDTO = repository.createChatRoomDTO(name);
        log.info("chatRoomId={}, chatRoomName={}",chatRoomDTO.getRoomId(), chatRoomDTO.getName());

        redirectAttributes.addAttribute("roomId", chatRoomDTO.getRoomId()); //<-- 쿼리파라미터로 담김
        return "redirect:/chat/room";
    }
}
