package com.tiffy.service;

import com.tiffy.dto.UserCreateDto;
import com.tiffy.entity.User;
import com.tiffy.repository.UserMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.crypto.SecretKey;
import java.util.Date;

@RequiredArgsConstructor
@Service
public class UserService {

    @Autowired
    private final UserMapper userMapper;

    @Autowired
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public String insertUser(@Valid UserCreateDto userCreateDto, BindingResult bindingResult) {

        if (!userCreateDto.getPassword1().equals(userCreateDto.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
            return "비밀번호 불일치";
        }

        try {
            User user = new User();
            user.setUsername(userCreateDto.getUsername());
            user.setEmail(userCreateDto.getEmail());
            user.setPassword(passwordEncoder.encode(userCreateDto.getPassword1()));
            user.setNickname(userCreateDto.getNickname());
            userMapper.insertUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            return "이미 등록된 사용자입니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
        return "회원가입 성공";
    }

    public boolean login(String username, String password) {
        try {
            // 사용자 인증을 시도
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            return authentication.isAuthenticated();  // 인증 성공 시 true 반환
        } catch (AuthenticationException e) {
            // 인증 실패 시 false 반환
            return false;
        }
    }

    public String generateToken(String username) {
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        // JWT 토큰 생성
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1일 유효기간 설정
                .signWith(key) // 시크릿 키로 서명
                .compact();
    }

    public User findUserById(Long id) {
        return userMapper.findUserById(id);
    }

    public User findUserByUsername(String username) {
        return userMapper.findUserByUsername(username);
    }
}
