package com.tiffy.controller;

import com.tiffy.dto.UserCreateDto;
import com.tiffy.entity.User;
import com.tiffy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public Map<String, Object> signup(@Valid @RequestBody UserCreateDto userCreateDto, BindingResult bindingResult) {
        Map<String, Object> response = new HashMap<>();
        String result = userService.insertUser(userCreateDto, bindingResult);

        if ("회원가입 성공".equals(result)) {
            response.put("status", "success");
            response.put("message", result);
            return response;
        }

        response.put("status", "error");
        response.put("message", result);

        return response;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> response = new HashMap<>();
        String username = loginData.get("username");
        String password = loginData.get("password");

        // 로그인 시도
        boolean isAuthenticated = userService.login(username, password);

        if (isAuthenticated) {
            String token = userService.generateToken(username);
            response.put("status", "success");
            response.put("message", "로그인 성공!");
            response.put("token", token);
            return response;
        }

        response.put("status", "error");
        response.put("message", "사용자 ID 또는 비밀번호를 확인해 주세요.");
        return response;
    }

    @PostMapping("/updatePlace")
    public Map<String, Object> exchangeUserPlaceUpdate(@AuthenticationPrincipal String username, @RequestBody Map<String, String> placeData) {
        return userService.updateUserPlace(username, placeData.get("place"));
    }

    @GetMapping("/findByLoginUserName")
    public User findByLoginUserName(@AuthenticationPrincipal String username) {
        return userService.findUserByUsername(username);
    }

    @GetMapping("/findUserByIncludeUserNickname")
    public List<User> searchUsers(@RequestParam String nickname) {
        return userService.findUserByIncludeUserNickname(nickname);
    }

    @GetMapping("/findUserByUserNickname")
    public User findUserByUserNickname(@RequestParam String nickname) {
        return userService.findUserByUserNickname(nickname);
    }

    @GetMapping("/findUserById")
    public User findUserById(@RequestParam Long id) {
        return userService.findUserById(id);
    }
}
