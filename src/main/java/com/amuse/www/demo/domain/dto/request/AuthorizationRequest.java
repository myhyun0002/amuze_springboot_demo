package com.amuse.www.demo.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/*
    로그인 시에 이메일과 비밀번호를 받아온다.
 */
@Getter
@Setter
public class AuthorizationRequest {
    @NotBlank(message = "이메일을 입력하세요.")
    private String username;
    @NotBlank(message = "패스워드를 입력하세요.")
    private String password;

    @Builder
    public AuthorizationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
