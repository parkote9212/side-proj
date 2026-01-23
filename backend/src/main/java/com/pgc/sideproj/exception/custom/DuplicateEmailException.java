package com.pgc.sideproj.exception.custom;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends RuntimeException {
    private final String email;

    public DuplicateEmailException(String email) {
        super(String.format("이미 존재하는 이메일입니다: %s", email));
        this.email = email;
    }
}
