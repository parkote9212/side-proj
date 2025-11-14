package com.pgc.sideproj.config;

import com.pgc.sideproj.filter.JwtAuthenticationFilter;
import com.pgc.sideproj.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    /**
     * PasswordEncoder Bean 등록 (비밀번호 암호화기)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt 알고리즘은 Spring Security에서 권장하는 해싱 방식입니다.
        return new BCryptPasswordEncoder();
    }

    /**
     * Spring Security의 핵심 설정인 SecurityFilterChain을 정의합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. CSRF 비활성화 (JWT를 사용하는 REST API는 세션을 사용하지 않으므로 CSRF 공격 위험이 낮습니다.)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. 세션 관리를 STATELESS(무상태)로 설정 (JWT 기반 인증의 핵심)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 3. HTTP 요청에 대한 접근 권한 설정
                .authorizeHttpRequests(authorize -> authorize
                        // 회원가입 및 로그인 API, 물건 조회 API는 인증 없이 모두 허용 (permitAll)
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/api/v1/items/**",
                                "/api/v1/statistics/**",

                                // --- Swagger UI 접근 허용 ---
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // 찜하기 API는 'USER' 권한을 가진 사용자만 허용
                        .requestMatchers(
                                "/api/v1/saved-items/**"
                        ).hasRole("USER")

                        .requestMatchers(
                                "/api/v1/admin/**",
                                "/api/admin/**" // BatchTestController 경로
                        ).hasRole("ADMIN")

                        // 그 외 모든 요청은 인증된 사용자(Authenticated)만 허용
                        .anyRequest().authenticated()
                )

                // 4. JWT 필터 추가: JWT 인증 필터를 UsernamePasswordAuthenticationFilter 전에 추가하여
                // 실제 인증 처리가 되기 전에 토큰을 검증하도록 합니다.
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

}
