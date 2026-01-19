package com.pgc.sideproj.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    // DB의 Primary Key
    private final Long id;

    // 사용자 이메일 (로그인 ID)
    private final String email;

    // 사용자 닉네임
    private final String nickname;

    // 사용자 권한 (예: USER, ADMIN)
    private final String role;

    // 보안상 password는 포함하지 않습니다.
}