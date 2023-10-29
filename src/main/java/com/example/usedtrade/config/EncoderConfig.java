package com.example.usedtrade.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//SecurityConfig에 같이 @Bean으로 등록하기보다는 별도로 설정하는게 좋음
//why? 순환참조 방지

@Configuration
public class EncoderConfig {

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
}
