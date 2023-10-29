package com.example.usedtrade.domain.user.entity;

import com.example.usedtrade.domain.base.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@EntityListeners(AuditingEntityListener.class) //이벤트 발생 감지 설정
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User extends BaseEntity {

    private String username;
    private String pwd;

    public void updatePw(String pwd) {
        this.pwd = pwd;
    }

}
