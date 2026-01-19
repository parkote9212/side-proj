package com.pgc.sideproj.dto.db;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Service;

@Getter
@Setter
@ToString
public class UserDTO {
    // Primary Key (Auto Increment)
    private Long id;

    // 사용자 이메일 (로그인 ID 역할, Unique)
    private String email;

    // 비밀번호 (해시값 저장)
    private String password;

    // 사용자 닉네임
    private String nickname;

    // 사용자 권한 (예: USER, ADMIN)
    private String role;
}
