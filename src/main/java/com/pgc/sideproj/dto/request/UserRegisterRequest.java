package com.pgc.sideproj.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String email;
    private String password;
    private String nickname;
    // role은 서비스에서 기본값으로 설정할 것이므로 요청 DTO에서는 제외
}