package com.example.usedtrade.domain.user.entity;

import com.example.usedtrade.domain.base.BaseEntity;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.FetchType;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class) //이벤트 발생 감지 설정
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "roleSet")
public class User extends BaseEntity {

    private String username;
    private String pwd;
    private String email;
    private boolean social;

    @ElementCollection(fetch= FetchType.LAZY)
    @Builder.Default
    private Set<UserRole> roleSet = new HashSet<>();


    public void updatePw(String pwd) {
        this.pwd = pwd;
    }

    public void addRole(UserRole userRole) {
        this.roleSet.add(userRole);
    }
}
