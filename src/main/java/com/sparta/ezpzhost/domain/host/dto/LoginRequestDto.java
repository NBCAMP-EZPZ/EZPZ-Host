package com.sparta.ezpzhost.domain.host.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequestDto {

    @NotBlank(message = "ID는 공백일 수 없습니다.")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    private String password;

}
