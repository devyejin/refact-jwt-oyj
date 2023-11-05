package com.example.usedtrade.security.filter;

import com.example.usedtrade.jwt.JwtTokenProvider;
import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

@RequiredArgsConstructor
@Log4j2
public class JwtAuthenticationFilter extends GenericFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        log.info("JwtAuthenticationFilter run!!!!!!");
        // 1. Request Header 에서 JWT 토큰 추출
        String token = resolveAccessToken((HttpServletRequest) request);

        // 2. validateToken 으로 토큰 유효성 검사
        if (token != null && jwtTokenProvider.validationToken(token)) {
            log.info("여기 로직 타니?");
            // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication={}",authentication.getPrincipal());
        }
        chain.doFilter(request, response);
    }

    // 기존에 JWT만 구현했을 때는 REST방식이라 "Authorization" 헤더 방식
//    private String resolveToken(HttpServletRequest request) {
//
//        String bearerToken = request.getHeader("Authorization");
//        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//
//
//    }

    private String resolveAccessToken(HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        if(cookies != null) {
            String accessToken = null;
            for(Cookie cookie : cookies) {
                if("grantType".equals(cookie.getName()) && "Bearer".equals(cookie.getValue())) {
                    accessToken = findAccessToken(cookies);
                    break;
                }
            }

            log.info("accessToken={}",accessToken);
            return accessToken;
        }

//        for(Cookie cookie : cookies) {
//            log.info("name={}, value={}", cookie.getName(), cookie.getValue());
//        }

        return null;
    }

    private String findAccessToken(Cookie[] cookies) {

        for(Cookie cookie : cookies) {
            if("accessToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }
}
