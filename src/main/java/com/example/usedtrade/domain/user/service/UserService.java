package com.example.usedtrade.domain.user.service;

import com.example.usedtrade.domain.user.repository.UserRepository;
import com.example.usedtrade.jwt.JwtTokenProvider;
import com.example.usedtrade.jwt.dto.TokenInfo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.Token;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenInfo login(String username, String pwd) {
        //username, pwd기반으로 Authentication 객체(UsernamePasswordAuthenticationToken) 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, pwd);

        //2. 실제 검증 authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);//Manager에게 위임하는 부분! -> 내부에서 laodByUsername..() 실행함, 실행하고 리턴된 결과
        //문제가 있다면 내부에서 exception터지고

        //여기까지 넘어왔다면 정상 처리 -> 권한부여된 token으로 Jwt생성해서 리턴
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authenticate);

        return tokenInfo;
    }
}
