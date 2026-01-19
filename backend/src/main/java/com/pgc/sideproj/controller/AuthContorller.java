package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.request.UserLoginRequest;
import com.pgc.sideproj.dto.request.UserRegisterRequest;
import com.pgc.sideproj.dto.response.TokenResponse;
import com.pgc.sideproj.dto.response.UserResponse;
import com.pgc.sideproj.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthContorller {

    private final UserService userService;

    /**
     * [POST] /api/v1/auth/register : 회원가입 엔드포인트
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @RequestBody UserRegisterRequest request
    ) {
        UserResponse response = userService.registerUser(request);
        // 201 Created 반환
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * [POST] /api/v1/auth/login : 로그인 엔드포인트
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody UserLoginRequest request
    ) {
        // 로그인 로직 실행 및 JWT 토큰 획득
        TokenResponse response = userService.login(request);
        // 200 OK와 함께 토큰 반환
        return ResponseEntity.ok(response);
    }

}
