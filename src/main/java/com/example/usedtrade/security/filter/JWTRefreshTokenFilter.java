package com.example.usedtrade.security.filter;

import com.example.usedtrade.security.exception.RefreshTokenException;
import com.example.usedtrade.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

/**
 * /refreshToken 요청시
 * -> access token 존재 여부 확인
 * -> refresh token 만료 여부 확인
 *      -> refresh token 만료됐다면, 재인증  -> access, refresh 재발급 응답
 *      -> refresh token 만료기간 임박 (3일) -> access, refresh 재발급 응답
 *      -> refresh token 기간 여유로움 -> access 재발급 응답
 *
 *
 * <구현 흐름>
 * 1. 전송된 JSON데이터에서 access, refresh 추출
 * 2. access validate -> access 가 없거나 잘못된 경우 -> 에러 메시지 전달
 * 3. refresh aclidate -> x or 잘못됨 or 만료 -> 에러 메시지 전달
 */
@RequiredArgsConstructor
@Log4j2
public class JWTRefreshTokenFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil; //생성자에서 주입됨
    private final String refreshPath;  //생성자에서 주입됨

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if(!path.equalsIgnoreCase(refreshPath)) {
            log.info("is not refreshpath, so skip JWTRefreshTokenFilter");
            filterChain.doFilter(request,response);
            return;
        }

        log.info("JWTRefreshTokenFilter run!");

        //request에서 access, refresh token 얻기
        Map<String,String> tokens = parseRequestJSON(request);

        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("accessToken={}",accessToken);
        log.info("refreshToken={}",refreshToken);

        //access token 검증
        try {
            checkAccessToken(accessToken);
        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return;
        }

        //access token 검증
        Map<String, Object> refreshClaims = null;

        try {
            refreshClaims = checkRefreshToken(refreshToken);
            log.info("refreshClaims={}",refreshClaims);

            //refreshToken 요효기간이 3일 미만인 경우 -> access, refresh 재발급
            Integer exp = (Integer) refreshClaims.get("exp");
            Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
            Date current = new Date(System.currentTimeMillis());

            long gapTime = expTime.getTime() - current.getTime();

            log.info("exp={}, expTime={}, current={}",exp ,expTime ,current);

            String username = (String)refreshClaims.get("username");

            //access재발급
            String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 1);

            String refreshTokenValue = tokens.get("refreshToken");
            //RefreshToken 기한이 3일 이내인 경우 -> 재발급
            if(gapTime < (1000 * 60 * 60 * 24 * 3)) {
                log.info("new refreshtoken generating....");
                refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 30);
            }

            log.info("accessTokenValue={}, refreshTokenNew={}",accessTokenValue ,refreshTokenValue);

            //재발급한 토큰(들) 전달
            sendTokens(accessTokenValue, refreshTokenValue, response);

        } catch (RefreshTokenException refreshTokenException) {
            refreshTokenException.sendResponseError(response);
            return; // 응답했으니 끝
        }


    }

    //request에서 access, refresh token 얻기
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {

        try(Reader reader = new InputStreamReader(request.getInputStream())) {

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class); // stream으로 읽어들인 후 -> Map으로 파싱

        }catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    private void checkAccessToken(String accessToken) throws RefreshTokenException {

        try {
            jwtUtil.validateToken(accessToken);
        }catch (ExpiredJwtException expiredJwtException) {
            log.error("Access Token has expired!!!"); //여기서 에러 잡아서 처리한거 아니니까, 다음 CATCH절로
        }catch (Exception exception) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS); //여기서 이제 클라이언트한테 만료됐다고 응답
        }

    }

    private Map<String,Object> checkRefreshToken(String refreshToken) throws RefreshTokenException{

        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);
            return values;
        } catch (ExpiredJwtException expiredJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        } catch (MalformedJwtException malformedJwtException) {
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        } catch (Exception exception) {
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }

        return null;
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue,
                "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
