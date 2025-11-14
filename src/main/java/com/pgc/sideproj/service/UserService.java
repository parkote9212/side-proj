package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.UserDTO;
import com.pgc.sideproj.dto.request.UserLoginRequest;
import com.pgc.sideproj.dto.request.UserRegisterRequest;
import com.pgc.sideproj.dto.response.TokenResponse;
import com.pgc.sideproj.dto.response.UserResponse;
import com.pgc.sideproj.mapper.UserMapper;
import com.pgc.sideproj.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 1. 회원가입 로직 (기존 구현 유지)
     */
    @Transactional
    public UserResponse registerUser(UserRegisterRequest request){
        //이메일 중복검증
        if(userMapper.findByEmail(request.getEmail()).isPresent()){
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        UserDTO user = new UserDTO();
        user.setEmail(request.getEmail());
        user.setPassword(encodedPassword);
        user.setNickname(request.getNickname());
        user.setRole("USER");

        userMapper.save(user);

        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }

    /**
     * 2. 로그인 로직
     * @param request 로그인 요청 DTO
     * @return 성공 시 JWT 토큰을 포함한 TokenResponse
     */
    public TokenResponse login(UserLoginRequest request) {

        // 1. 이메일로 사용자 조회
        Optional<UserDTO> userOptional = userMapper.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            // 커스텀 예외로 "사용자를 찾을 수 없음" 처리 필요
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        UserDTO user = userOptional.get();

        // 2. 비밀번호 검증 (입력된 평문 비밀번호와 DB의 해시된 비밀번호 비교)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            // 커스텀 예외로 "비밀번호 불일치" 처리 필요
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. 검증 성공 시 JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        // 4. 토큰 응답 DTO 반환
        return TokenResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .build();
    }

    /**
     * [추가] 관리자용: 모든 사용자 목록 조회
     * @return UserResponse 리스트 (비밀번호 제외)
     */
    public List<UserResponse> getAllUsers(){
        List<UserDTO> users = userMapper.findAll();
        
        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    /**
     * [추가] UserDTO를 UserResponse로 변환하는 헬퍼 메소드
     */

    private UserResponse convertToUserResponse(UserDTO user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}
