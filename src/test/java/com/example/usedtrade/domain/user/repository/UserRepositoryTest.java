package com.example.usedtrade.domain.user.repository;

import com.example.usedtrade.domain.user.entity.User;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Log4j2
class UserRepositoryTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testInserts() {
        IntStream.rangeClosed(1,10).forEach(i -> {
            User user = User.builder()
                    .username("testUser" + i)
                    .pwd(passwordEncoder.encode("password1234"))
                    .build();

            userRepository.save(user);
        });
    }

}