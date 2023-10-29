package com.example.usedtrade.config;

import com.example.usedtrade.domain.user.entity.User;
import com.example.usedtrade.domain.user.service.JwtUserDetailsService;
import com.example.usedtrade.security.filter.JWTCheckFilter;
import com.example.usedtrade.security.filter.JWTLoginFilter;
import com.example.usedtrade.security.filter.JWTRefreshTokenFilter;
import com.example.usedtrade.security.handler.JwtLoginSuccessHandler;
import com.example.usedtrade.security.handler.SocialLoginSuccessHandler;
import com.example.usedtrade.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@Log4j2
@EnableMethodSecurity(prePostEnabled = true)  // @EnableGlobalMethodSecurity는 deprecated, 메서드단위로 인가 체크 어노테이션 사용 가능 하도록 설정
@RequiredArgsConstructor
public class CustomSecurityConfig {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new SocialLoginSuccessHandler(passwordEncoder);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        //AuthenticationManager 설정
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);

        authenticationManagerBuilder // docs보면, AuthenticationProvider랑 UserDetailsService를 이용해서 AuthenticationManager를 만듬
                .userDetailsService(jwtUserDetailsService)
                .passwordEncoder(passwordEncoder);

        //Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        //시큐리티 아키텍처에서보면, 뒷 단에 필요한 Provider, UserDetailsService들이 설정된 authenticationManger를 설정!해줌
        http.authenticationManager(authenticationManager);


        //JwtAuthenticationFilter설정 (<- 순으로)
        JWTLoginFilter jwtLoginFilter = new JWTLoginFilter("/generateToken");
        jwtLoginFilter.setAuthenticationManager(authenticationManager); //필요한 authenticationManager를 filter에 설정

        //JwtLoginSuccessHandler 설정
        JwtLoginSuccessHandler successHandler = new JwtLoginSuccessHandler(jwtUtil);
        jwtLoginFilter.setAuthenticationSuccessHandler(successHandler);

        http
                //http에 filter 설정 -> UsernamePass..Filter보다 jwtLoginFilter(custom) 먼저 동작
                .addFilterBefore(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class)

                // "/api/"로 시작하는 모든 요청은 JWTCheckFilter 동작
                .addFilterBefore(jwtCheckFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                // JWTCheckFilter (토큰 확인전에 만료여부 부터 체크)
                .addFilterBefore(new JWTRefreshTokenFilter(jwtUtil,"/refreshToken"),
                        JWTCheckFilter.class)

                //oauth2 + kakao
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/user/login")
                        .successHandler(authenticationSuccessHandler()))

                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests) -> {
                    authorizeRequests.anyRequest().permitAll(); //우선 권한에 대한 부분 생략
                })
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) //RESTAPI 방식이라 Session 생성 안함
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }

    private JWTCheckFilter jwtCheckFilter(JWTUtil jwtUtil) {
        return new JWTCheckFilter(jwtUtil);
    }


}
