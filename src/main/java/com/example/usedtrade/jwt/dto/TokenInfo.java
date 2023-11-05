package com.example.usedtrade.jwt.dto;

import lombok.*;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TokenInfo {

    private String grantType; //JWT 인증 타입(HTTP헤더 prefix) JWT는 Bearer 사용
    private String accessToken;
    private String refreshToken;
}
