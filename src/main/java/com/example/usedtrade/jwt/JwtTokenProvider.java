package com.example.usedtrade.jwt;

import com.example.usedtrade.jwt.dto.TokenInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

//로직 변경
//기존방식은 access 1일, refresh 30일로 처리했었는데 => 권장은 동일한 날짜라고 함
@Component
@Log4j2
public class JwtTokenProvider {

    private final Key key; //String타입으로 했었는데 Key타입으로 변경

    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }


//    public String generateToken(Map<String, Object> valueMap, int days) { // JWT는 문자열
//        log.info("running generateToken..." + key);
//
//        //header
//        HashMap<String, Object> headers = new HashMap<>();
//        headers.put("typ","JWT");
//        headers.put("alg","HS256");
//
//        //payload (찐데이터)
//        HashMap<String, Object> payload = new HashMap<>();
//        payload.putAll(valueMap); // Map.putAll => Map -> Map 데이터 옮겨줌
//
//        //유효시간
//        int time = (60 * 24) * days; //유효기간 1일
//
//        return Jwts.builder()
//                .setHeader(headers)
//                .setClaims(payload)
//                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))
//                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(time).toInstant()))
//                .signWith(SignatureAlgorithm.HS256, key.getBytes())
//                .compact();
//    }

    //사용자 정보로 토큰 생성
    public TokenInfo generateToken(Authentication authentication) {

        if (authentication.getAuthorities().isEmpty()) {
            authentication.getAuthorities().add(new SimpleGrantedAuthority("USER_ROLE"));
        }


        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + 86400000); //1일
        String accessToken  = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();


        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + 86400000))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
        
        return TokenInfo.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

    }

    //Jwt 토큰 복호화, 정보 꺼내는 메서드
    public Authentication getAuthentication(String accessToken) {
        //복호화
        Claims claims = parseClaims(accessToken);
        log.info("claims.auth={}", claims.get("auth"));

        if(claims.get("auth") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        //클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .toList();

        //UserDtails 객체로 만들어서 반환
        UserDetails principal  = new User(claims.getSubject(), "", authorities);//(username,pwd,authorites)
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    private Claims parseClaims(String accessToken) {

        try {
            return Jwts.parser().setSigningKey(key).parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
        
    }


    //토큰 검증 메서드
    public boolean validationToken(String token) {

        try {
             Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            //parseClaimsJws(token) 과정에서 아래케이스 걸러 줌
            // ExpiredJwtException, UnsupportedJwtException, MalformedJwtException, SignatureException, IllegalArgumentException;
            return true; // Exception발생 안하면 제대로된 토큰
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty.", e);
        }
        return false;
    }

//    //JWT검증시 여러 예외가 발생할 수 있는데 상위 JwtException으로 처리
//    public Map<String,Object> validateToken(String token) throws JwtException {
//        Map<String,Object> claim = null;
//
//         claim = Jwts.parser()
//                .setSigningKey(key.getBytes()) // Set key
//                .parseClaimsJws(token) //token 파싱 및 검증 -> 실패시 에러 발생
//                .getBody();
//
//        return claim;
//    }


}
