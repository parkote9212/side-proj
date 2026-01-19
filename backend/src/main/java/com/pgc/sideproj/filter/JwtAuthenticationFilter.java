package com.pgc.sideproj.filter;

import com.pgc.sideproj.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 실제 필터링 로직이 구현되는 메소드
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String jwt = resolveToken(request);

        // 2. 토큰 유효성 검사
        if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {

            // 3. 토큰이 유효하면 인증 객체를 가져옴
            Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

            // 4. Spring Security Context에 인증 정보 저장 (인증 완료)
            // 이후 해당 요청이 끝날 때까지 @AuthenticationPrincipal 등으로 인증 정보를 사용할 수 있게 됩니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 다음 필터 또는 서블릿으로 요청을 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 JWT 토큰을 추출하는 헬퍼 메소드
     * @param request HTTP 요청
     * @return 추출된 JWT (Bearer 접두사 제거)
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        // "Authorization: Bearer <token>" 형태인지 확인하고 "Bearer "를 제거한 순수 토큰 반환
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
