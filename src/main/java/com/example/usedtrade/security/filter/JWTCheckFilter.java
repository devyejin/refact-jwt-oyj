package com.example.usedtrade.security.filter;

import com.example.usedtrade.security.exception.AccessTokenException;
import com.example.usedtrade.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * JWTCheckFilter는 OncePerRequestFilter 를 구현
 * OncePerRequestFilter : 하나의 요청(동일한 요청)에 대해서 한 번씩 동작하는 필터
 * 일반 Filter의 경우, 동일한 요청이 여러번 호출시 매번 Filter가 호출 될 수 있는데, OncePerRequestFilter 는 한 번만 호출되는게 보장됨
 * -> 동일한 요청에 대해 사용자 인증을 계속하는건 비효율적이니 OncePerRequestFilter 구현!
 *
 * 1. Access 없으면 -> 토큰 없다는 메시지 전달
 * 2. Access 토큰이 문제가 있다면 -> 잘못된 토큰이라는 메시지 전달
 * 3. Access 토큰 만료시 -> 갱신하라는 메시지 전달
 *
 * request headers 중 'Authorization' header 값으로 토큰이 전달됨
 * 'type + 인증값(jwt)' 인데, JWT, OAUTH는 'Bearer' type임
 *
 */
@RequiredArgsConstructor
@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if(!path.startsWith("/api/")) {
            filterChain.doFilter(request,response);
            return;
        }

        log.info("JWT Check Filter run");
        log.info("jwtUtil={}",jwtUtil);


        filterChain.doFilter(request,response);
    }


    private Map<String,Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization");

        if(headerStr == null || headerStr.length() < 8) { //token길이가 8글자보다 작을 수가 없어서 거르기
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        String tokenType = headerStr.substring(0, 6); // Bearer
        String tokenStr = headerStr.substring(7);

        if(!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);
            return values;
        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);
        } catch (SignatureException signatureException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);
        } catch (ExpiredJwtException expiredJwtException) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }



    }
}
