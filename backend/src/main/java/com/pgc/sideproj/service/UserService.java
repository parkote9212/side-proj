package com.pgc.sideproj.service;

import com.pgc.sideproj.dto.db.UserDTO;
import com.pgc.sideproj.dto.request.UserLoginRequest;
import com.pgc.sideproj.dto.request.UserRegisterRequest;
import com.pgc.sideproj.dto.response.TokenResponse;
import com.pgc.sideproj.dto.response.UserResponse;
import com.pgc.sideproj.exception.custom.BadCredentialsException;
import com.pgc.sideproj.exception.custom.DuplicateEmailException;
import com.pgc.sideproj.mapper.UserMapper;
import com.pgc.sideproj.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스입니다.
 * 
 * <p>회원가입, 로그인, 사용자 조회 등의 기능을 제공합니다.
 * 비밀번호는 암호화되어 저장되며, 로그인 시 JWT 토큰이 발급됩니다.
 * 
 * @author sideproj
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 새로운 사용자를 등록합니다.
     * 
     * <p>이메일 중복 검증을 수행하며, 비밀번호는 BCrypt로 암호화되어 저장됩니다.
     * 기본 역할은 "USER"로 설정됩니다.
     * 
     * @param request 회원가입 요청 정보 (이메일, 비밀번호, 닉네임)
     * @return 생성된 사용자 정보 (비밀번호 제외)
     * @throws DuplicateEmailException 이메일이 이미 존재하는 경우
     */
    @Transactional
    public UserResponse registerUser(UserRegisterRequest request){
        //이메일 중복검증
        if(userMapper.findByEmail(request.getEmail()).isPresent()){
            throw new DuplicateEmailException(request.getEmail());
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
     * 사용자 로그인을 처리하고 JWT 토큰을 발급합니다.
     * 
     * <p>이메일과 비밀번호를 검증한 후, 성공 시 JWT 액세스 토큰을 생성하여 반환합니다.
     * 발급된 토큰은 이후 API 호출 시 Authorization 헤더에 포함하여 사용합니다.
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return JWT 토큰 정보 (accessToken, tokenType)
     * @throws BadCredentialsException 이메일 또는 비밀번호가 일치하지 않는 경우
     */
    public TokenResponse login(UserLoginRequest request) {

        // 1. 이메일로 사용자 조회
        Optional<UserDTO> userOptional = userMapper.findByEmail(request.getEmail());

        if (userOptional.isEmpty()) {
            throw new BadCredentialsException();
        }

        UserDTO user = userOptional.get();

        // 2. 비밀번호 검증 (입력된 평문 비밀번호와 DB의 해시된 비밀번호 비교)
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException();
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
     * 시스템에 등록된 모든 사용자 목록을 조회합니다.
     * 
     * <p>관리자 전용 기능으로, 모든 사용자의 정보를 조회할 수 있습니다.
     * 비밀번호는 응답에 포함되지 않습니다.
     * 
     * @return 모든 사용자 정보 목록 (UserResponse 리스트, 비밀번호 제외)
     */
    public List<UserResponse> getAllUsers(){
        List<UserDTO> users = userMapper.findAll();

        return users.stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    /**
     * UserDTO를 UserResponse로 변환하는 헬퍼 메서드입니다.
     * 
     * <p>비밀번호는 변환 과정에서 제외됩니다.
     * 
     * @param user 변환할 UserDTO 객체
     * @return UserResponse 객체 (비밀번호 제외)
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
