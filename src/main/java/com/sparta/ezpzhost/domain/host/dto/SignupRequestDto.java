package com.sparta.ezpzhost.domain.host.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {

    @NotBlank(message = "ID는 공백일 수 없습니다.")
    @Size(min = 6, max = 20, message = "ID는 최소 6자 이상, 최대 20자 이하이어야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영문 또는 숫자만 가능합니다.")
    private String username;

    @NotBlank(message = "비밀번호는 공백일 수 없습니다.")
    @Size(min = 10, message = "비밀번호는 최소 10자 이상이어야 합니다.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{10,}$",
            message = "비밀번호는 최소 10자 이상이어야 하며, 영문 대소문자, 숫자, 특수문자를 최소 1글자씩 포함해야 합니다.")
    private String password;

    @NotBlank(message = "이메일은 공백일 수 없습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "이메일 형식이 맞지 않습니다.")
    private String email;

    @NotBlank(message = "기업명은 공백일 수 없습니다.")
    @Pattern(regexp = "^[가-힣a-zA-Z\\s]+$", message = "이름은 한글과 영어와 띄어쓰기만 포함할 수 있습니다.")
    private String companyName;

    @NotBlank(message = "사업자 번호는 공백일 수 없습니다.")
    private String businessNumber;

}
