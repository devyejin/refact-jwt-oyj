package com.example.usedtrade.domain.user.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
/*
    JwtUserDetailsService의 loadUserByUsername() 결과를 처리하기 위한 dto
    security.core.userdetails.User 클래스는 UserDetails 인터페이스를 구현한 클래스, 직접 UserDetails구현하기 번거롭기 때문에 제공하는 User 클래스 이용
 */

@Getter
@Setter
@ToString
public class UserDTO extends User {

    private String username;
    private String pwd;

    public UserDTO(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.username = username;
        this.pwd = password;
    }


}
