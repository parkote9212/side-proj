package com.pgc.sideproj.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration-time}")
    private long expirationTime;

    private Key key;

    /**
     * @PostConstruct: 필드 주입이 완료된 후, 초기화 작업을 수행합니다.
     * 문자열 타입의 비밀키를 서명에 사용할 Key 객체로 변환합니다.
     */

    @PostConstruct
    protected void init() {
        // --- 1. 진단 로그 추가 ---
        log.info("JwtTokenProvider.init() 시작");
        if (secretKey == null || secretKey.isBlank()) {
            log.error("치명적 오류: jwt.secret-key가 로드되지 않았습니다! (secretKey is NULL or empty)");
            // 여기서 예외를 발생시켜 앱 시작을 중단시키는 것이 좋습니다.
            throw new IllegalArgumentException("jwt.secret-key가 설정되지 않았습니다.");
        } else {
            log.info("jwt.secret-key 로드 성공. (길이: {}자)", secretKey.length());
        }

        // --- 2. 기존 로직 (try-catch 추가) ---
        try {
            byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
            this.key = Keys.hmacShaKeyFor(keyBytes);
            log.info("Key 객체 생성 성공 (this.key is not null)");
        } catch (Exception e) {
            log.error("Key 객체 생성 중 치명적 오류 발생", e);
            // e.g., 키 길이가 너무 짧으면 WeakKeyException 발생
        }
    }

    /**
     * JWT 토큰을 생성합니다.
     *
     * @param email 사용자 이메일 (Subject)
     * @param role  사용자 권한 (Claim)
     * @return 생성된 JWT String
     */

    public String createToken(String email, String role) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("role", role);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 토큰에서 이메일과 권한 정보를 추출하여 Spring Security의 Authentication 객체를 생성합니다.
     *
     * @param token JWT
     * @return Authentication 객체
     */

    public Authentication getAuthentication(String token) {
        // 토큰 파싱 및 Claims 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        //권한 정보 추출("ROLE_USER")
        String roleString = claims.get("role", String.class);

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + roleString)
        );

        // Spring Security용 Authentication 객체 생성
        return new UsernamePasswordAuthenticationToken(claims.getSubject(), null, authorities);
    }

    /**
     * 토큰의 유효성을 검증합니다.
     *
     * @param token JWT
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.info("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.info("JWT 토큰이 잘못되었습니다.");
        }
        return false;
        }
    }
