package com.example.usedtrade.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

/**
 * 클라이언트가 username, pwd를 통해 로그인을 하면 JWT를 발행하도록 필터에서 처리
 * -> AbstractAuthenticationProcessingFilter 상속받는데, 생성자와 추상메서드 attemptAuthentication 오버라이드!
 *
 */

@Log4j2
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
    public JWTLoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("JWTLoginFilter run...");

        //POST요청으로 Login시에만 JWT발행
        if(request.getMethod().equalsIgnoreCase("GET")) {
            log.info("get method not support login");

            return null;
        }

        Map<String, String> jsonLoginData = parseRequestJSON(request);
        log.info("jsonLoginData={}",jsonLoginData);

        //username, pwd 기반으로 token 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(jsonLoginData.get("username"),
                jsonLoginData.get("pwd"));

        //시큐리티는 토큰인증기반! 사용자정보를 토큰으로 만듬 -> AuthenticationManager 한테 전달 -> 사용자 인증 완료되면 Authentication(principal + authories) 반환, 인증 실패시 Exception
        //우선은 AuthenticationManager 가 일할 수 있도록 정보를 Token으로 만든겨! (jwt토큰 아님)
        return getAuthenticationManager().authenticate(authenticationToken);

    }

    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        // request JSON에서 username, pwd Map으로 parsing
        try(Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);

        }catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }
}
