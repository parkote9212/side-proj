package com.pgc.sideproj.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다")
    @Size(min = 8, max = 50, message = "비밀번호는 8자 이상 50자 이하여야 합니다")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "비밀번호는 영문 대소문자와 숫자를 포함해야 합니다")
    private String password;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9_-]+$", message = "닉네임은 한글, 영문, 숫자, 언더스코어, 하이픈만 사용 가능합니다")
    private String nickname;

    // role은 서비스에서 기본값으로 설정할 것이므로 요청 DTO에서는 제외
}
