//package com.example.usedtrade.security.handler;
//
//import com.example.usedtrade.jwt.JwtTokenProvider;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.http.MediaType;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
//
//import java.io.IOException;
//import java.util.Map;
//
///**
// * 인증처리된 후, Jwt Access, Refresh Token 응답해주자!
// *
// * jwt 생성과 클라이언트로부터 넘어온 jwt 확인 => jjwt 라이브러리 적용
// */
//
//@Log4j2
//@RequiredArgsConstructor
//public class JwtLoginSuccessHandler implements AuthenticationSuccessHandler {
//
//    private final JwtTokenProvider jwtTokenProvider;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
//        //AuthenticationManager 가 Username..Token을 이용해서 인증해보니 -> 존재하는 회원 -> Authentication 가지고 성공 로직 만들어보아요
//        //마찬가지로, 이 Handler가 Security 에서 적용되야하니 Config에 등록해줘야함
//        log.info("Jwt Login Success Handler run.......");
//
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        log.info("authentication={}",authentication);
//        log.info("username={}",authentication.getName());
//
//        //AccessToken 발행 (username 기반으로)
//        Map<String, Object> claim = Map.of("username", authentication.getName());
//        String accessToken = jwtTokenProvider.generateToken(claim, 1); //AccessToken 유효기간 1일
//
//        //RefreshToken 발행
//        String refreshToken = jwtTokenProvider.generateToken(claim, 30);//RefreshToken 유효기간 30일
//
////        Gson gson = new Gson();
////
////        Map<String, Object> keyMap = Map.of("accessToken", accessToken,
////                "refreshToken", refreshToken);
////
////        String jsonStr = gson.toJson(keyMap);
////        response.getWriter().println(jsonStr); // 로그인 성공시 Jwt 응답 <- 단순 테스트용 출력
//        //chat 구현 위해 쿠키 저장
//        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//        accessTokenCookie.setPath("/");
//        response.addCookie(accessTokenCookie);
//
//        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//        refreshTokenCookie.setPath("/");
//        response.addCookie(refreshTokenCookie);
//
//    }
//}
