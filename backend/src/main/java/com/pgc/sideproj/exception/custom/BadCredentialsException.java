package com.pgc.sideproj.exception.custom;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    public BadCredentialsException(String message) {
        super(message);
    }
}
