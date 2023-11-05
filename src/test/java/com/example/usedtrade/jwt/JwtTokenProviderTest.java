package com.example.usedtrade.jwt;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

/**
 * JwtTokenProvider 이용해서 JWT문자열 생성
 * 생성된 문자열 jwt.io 사이트 이용해서 정상 JWT인지 체크
 * JWTUtil의 validateToken() 메서드를 통해 jwt.io 사이트 결과와 일치하는지 체크
 */
@SpringBootTest
@Log4j2
class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void GenerateTokenTest() {
        Map<String, Object> claimMap = Map.of("id", "abcd");
        String jwtStr = jwtTokenProvider.generateToken(claimMap, 1);
        log.info("jwtStr={}",jwtStr);
    }

    @Test
    @DisplayName("유효기간 지난 JWT -> JwtException발생")
    public void testValidate() {
        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE2OTg1MTQxMjAsImlhdCI6MTY5ODUxNDA2MCwiaWQiOiJhYmNkIn0.vmrE2-zQXdPUDygCj8V196Az1whqHvsZTONRhuN5vbA";

        Map<String, Object> claim = jwtTokenProvider.validateToken(jwtStr);
        log.info("claim={}",claim); //claim={exp=1698514120, iat=1698514060, id=abcd}
    }


    @Test
    public void testAll() {

        String jwtStr = jwtTokenProvider.generateToken(Map.of("username", "AAA", "email", "abc@naver.com"), 1);
        log.info("jwtStr={}",jwtStr);

        Map<String, Object> claim = jwtTokenProvider.validateToken(jwtStr);
        log.info("username={}",claim.get("username"));
        log.info("email={}",claim.get("email"));
        log.info("claim={}",claim); //  claim={exp=1698600783, iat=1698514383, email=abc@naver.com, username=AAA}

        //맨 처음한 테스트랑 보면, 내가 claim으로 넣는것들을 기반으로 jwt payload가 구성되는걸 알 수 있뜨아!
    }
}