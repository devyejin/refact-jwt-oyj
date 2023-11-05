package com.example.usedtrade.domain.user.entity;

import com.example.usedtrade.domain.base.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class) //이벤트 발생 감지 설정
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class User  extends BaseEntity implements UserDetails {

    @Column(updatable = false, unique = true, nullable = false)
    private String username;

    @NotBlank
    private String pwd;

    @Email
    private String email;
    private boolean social;

    @ElementCollection(fetch= FetchType.LAZY)
    @Builder.Default
    private List<String> roles = new ArrayList<>();


//    public void updatePw(String pwd) {
//        this.pwd = pwd;
//    }

//    public void addRole(UserRole userRole) {
//        this.roles.add(userRole);
//    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return pwd;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
