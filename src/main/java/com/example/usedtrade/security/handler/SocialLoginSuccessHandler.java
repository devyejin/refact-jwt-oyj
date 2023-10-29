package com.example.usedtrade.security.handler;

import com.example.usedtrade.domain.user.dto.UserDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;


@RequiredArgsConstructor
@Log4j2
public class SocialLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final PasswordEncoder passwordEncoder;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("SocialLoginSuccessHandler run!!");
        log.info("authentication={}", authentication);

        UserDTO userDTO = (UserDTO) authentication.getPrincipal();
        String encodedPw = userDTO.getPwd();

        //소셜 가입해서 임시비밀번호 tempPwd로 가입된 상태 -> 비번 수정하라고 로직 처리
        if(userDTO.isSocial() && userDTO.getPwd().equalsIgnoreCase("tempPwd")
        || passwordEncoder.matches("tempPwd", userDTO.getPwd())) {
            log.info("User Should Change Password!!!!");
            //리다이렉트 시키기
            response.sendRedirect("/user/modify");
            return;
        }else {
            response.sendRedirect("/"); //default로 리다이렉트
        }
    }
}
