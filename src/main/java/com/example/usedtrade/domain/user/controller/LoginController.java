package com.example.usedtrade.domain.user.controller;

import com.example.usedtrade.domain.user.dto.LoginFormDTO;
import com.example.usedtrade.domain.user.service.UserService;
import com.example.usedtrade.jwt.dto.TokenInfo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Controller
@RequestMapping("/user")
@Log4j2
@RequiredArgsConstructor
public class LoginController {

    private final UserService userService;

    @GetMapping("/loginForm")
    public String login() {
        return "user/loginForm";
    }

    @PostMapping("/login")
    public void login(@RequestBody LoginFormDTO loginFormDTO, HttpServletResponse response) throws IOException {
        String username = loginFormDTO.getUsername();
        String pwd = loginFormDTO.getPwd();
        TokenInfo tokenInfo = userService.login(username, pwd);

        Cookie grantTypeCookie = new Cookie("grantType", tokenInfo.getGrantType());
        grantTypeCookie.setPath("/");
        grantTypeCookie.setHttpOnly(true);


        Cookie accessToken = new Cookie("accessToken", tokenInfo.getAccessToken());
        accessToken.setPath("/"); //루트 경로 설정시 전체 애플리케이션에서 쿠키 접근 가능(리다이렉트페이지로설정x)
        accessToken.setHttpOnly(true); //보안, js접근 방지

        Cookie refreshToken = new Cookie("refreshToken", tokenInfo.getRefreshToken());
        refreshToken.setPath("/");
        refreshToken.setHttpOnly(true);

        response.addCookie(grantTypeCookie);
        response.addCookie(accessToken);
        response.addCookie(refreshToken);

        log.info("accessToken={}, refreshToken={} ",accessToken, refreshToken);

        response.sendRedirect("/");



    }

    //로그인은 필터가 아니라 인증만 필터아닌가?
    //Post 요청으로 로그인되는 부분은 UsernamePasswordAuthenticationFilter 에서 처리
    //JWTLoginFilter (구현체)
}
