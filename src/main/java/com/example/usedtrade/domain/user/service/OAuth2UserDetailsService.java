package com.example.usedtrade.domain.user.service;

import com.example.usedtrade.domain.user.dto.UserDTO;
import com.example.usedtrade.domain.user.entity.User;
import com.example.usedtrade.domain.user.entity.UserRole;
import com.example.usedtrade.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class OAuth2UserDetailsService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("userRequest={}",userRequest);

        ClientRegistration clientRegistration = userRequest.getClientRegistration();
        String clientName = clientRegistration.getClientName();

        log.info("clientName={}",clientName);
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> paramMap = oAuth2User.getAttributes();

        String email = null;

        switch (clientName) {
            case "kakao" :
                email = getKakaoEmail(paramMap);
                break;
        }

        log.info("email={}",email);

        return generateDTO(email,paramMap);
    }

    private String getKakaoEmail(Map<String, Object> paramMap) {
        log.info("kakao oauth2 run..");

        Object value = paramMap.get("kakao_account");
        log.info("value={}",value);

        LinkedHashMap accountMap = (LinkedHashMap) value;
        String email = (String)accountMap.get("email");
        log.info("email={}",email);

        return email;

    }

    private UserDTO generateDTO(String email, Map<String, Object> params) {
        Optional<User> result = userRepository.findByEmail(email);

        if(result.isEmpty()) {
            //회원 가입 시키기
            User user = User.builder()
                    .username(email)
                    .pwd(passwordEncoder.encode("tempPwd")) //uuid나 랜덤값으로 변경 후 -> 회원이 비번 변경하도록 페이지 리다이렉트
                    .email(email)
                    .social(true)
                    .build();

//            user.addRole(UserRole.USER);

            userRepository.save(user);

            //UserDTO 반환
            UserDTO userDTO = new UserDTO(email, "tempPwd", email, true, Arrays.asList(new SimpleGrantedAuthority("ROLE_USER")));
            userDTO.setProps(params); // 소셜 로그인 정보

            return userDTO;
        } else {
            //가입된 회원에 존재하는 email인 경우 -> 소셜 정보만 추가해주기
//            User user = result.get();
//            UserDTO userDTO = new UserDTO(user.getUsername(), user.getPwd(), user.getEmail(), true, user.getRoles().stream()
//                    .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.name()))
//                    .collect(Collectors.toList())
//            );

//            return userDTO;
            // jwt로직변경하면서 addRole 제거해서! 여기도 수정해야함-------------------------
            return null;
        }
    }
}
