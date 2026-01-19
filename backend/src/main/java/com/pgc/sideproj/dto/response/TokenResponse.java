package com.pgc.sideproj.dto.response;

import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {
    // 발급된 Access Token
    private String accessToken;
    // 토큰 타입 (JWT의 경우 "Bearer"를 주로 사용)
    @Default
    private String tokenType = "Bearer";
}