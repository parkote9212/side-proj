package com.pgc.sideproj.controller;

import com.pgc.sideproj.dto.request.UserLoginRequest;
import com.pgc.sideproj.dto.request.UserRegisterRequest;
import com.pgc.sideproj.dto.response.TokenResponse;
import com.pgc.sideproj.dto.response.UserResponse;
import com.pgc.sideproj.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 인증 관련 API 컨트롤러입니다.
 * 
 * <p>회원가입 및 로그인 기능을 제공합니다.
 * 로그인 성공 시 JWT 토큰이 발급되며, 이후 API 호출 시 이 토큰을 사용하여 인증합니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthContorller {

    private final UserService userService;

    /**
     * 새로운 사용자를 등록합니다.
     * 
     * <p>이메일 중복 검증을 수행하며, 비밀번호는 암호화되어 저장됩니다.
     * 기본 역할은 "USER"로 설정됩니다.
     * 
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임)
     * @return 생성된 사용자 정보 (비밀번호 제외)
     * @throws com.pgc.sideproj.exception.custom.DuplicateEmailException 이메일이 이미 존재하는 경우
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody UserRegisterRequest request
    ) {
        UserResponse response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
     * 
     * <p>이메일과 비밀번호를 검증한 후, 성공 시 JWT 액세스 토큰을 반환합니다.
     * 발급된 토큰은 이후 API 호출 시 Authorization 헤더에 포함하여 사용합니다.
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return JWT 토큰 정보 (accessToken, tokenType)
     * @throws com.pgc.sideproj.exception.custom.BadCredentialsException 이메일 또는 비밀번호가 일치하지 않는 경우
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @RequestBody UserLoginRequest request
    ) {
        TokenResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
