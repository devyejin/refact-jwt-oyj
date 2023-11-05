package com.example.usedtrade.domain.user.service;

import com.example.usedtrade.domain.user.dto.UserDTO;
import com.example.usedtrade.domain.user.entity.User;
import com.example.usedtrade.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        //로그인할 때 form에서 넘어오는 id, pwd로 찾는 부분임
        //회원이 존재하면, UserDTO반환
//        User user = userRepository.findByUsername(username).
//                orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다."));
//
//        log.info("회원 정보가 존재해서 회원객체 dto 반환");
//
//        UserDTO userDTO = new UserDTO(
//                user.getUsername(),
//                user.getPwd(),
//                user.getEmail(),
//                false,
//                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")) //우선 모든 사용자에 USER권한 부여하는 방식으로 처리
//        );
//
//        log.info("userDTO={}", userDTO);
//
//        return userDTO;
        return userRepository.findByUsername(username)
                .map(this::createUserDetails) // findByUsername(username)수행결과 반환된 Optional에 값이 있으면 create..실행 -> 없으면 예외던짐
                .orElseThrow(() -> new UsernameNotFoundException("해당하는 유저를 찾을 수 없습니다."));
    }

    private UserDetails createUserDetails(User user) {
        return User.builder()
                .username(user.getUsername())
                .pwd(passwordEncoder.encode(user.getPassword()))
                .roles((List.of(user.getRoles().toArray(new String[0]))))
                .build();
    }
}
