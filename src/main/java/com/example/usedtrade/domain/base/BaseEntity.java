package com.example.usedtrade.domain.base;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@EntityListeners(AuditingEntityListener.class) //이벤트 발생 감지
@MappedSuperclass //별개의 테이블로 생성하지 않음, 일반 클래스에서 상속받을 수 있도록 설정
public class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(updatable = false)
    @CreatedDate //엔티티 저장될 때 시간 자동 저장
    private LocalDateTime createdAt;

    @Column(updatable = true) //default
    @LastModifiedDate //엔티티 값 변경될 때 시간 자동 저장
    private LocalDateTime updatedAt;
}
