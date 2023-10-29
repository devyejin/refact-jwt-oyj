package com.example.usedtrade.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Log4j2
public class JWTUtil {

    @Value("${jwt.secret}")
    private String key;

    public String generateToken(Map<String, Object> valueMap, int days) { // JWT는 문자열
        log.info("running generateToken..." + key);

        //header
        HashMap<String, Object> headers = new HashMap<>();
        headers.put("typ","JWT");
        headers.put("alg","HS256");

        //payload (찐데이터)
        HashMap<String, Object> payload = new HashMap<>();
        payload.putAll(valueMap); // Map.putAll => Map -> Map 데이터 옮겨줌

        //유효시간
        int time = (60 * 24) * days; //유효기간 1일

        return Jwts.builder()
                .setHeader(headers)
                .setClaims(payload)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
                .signWith(SignatureAlgorithm.HS256, key.getBytes())
                .compact();
    }

    //JWT검증시 여러 예외가 발생할 수 있는데 상위 JwtException으로 처리
    public Map<String,Object> validateToken(String token) throws JwtException {
        Map<String,Object> claim = null;

         claim = Jwts.parser()
                .setSigningKey(key.getBytes()) // Set key
                .parseClaimsJws(token) //token 파싱 및 검증 -> 실패시 에러 발생
                .getBody();

        return claim;
    }


}
