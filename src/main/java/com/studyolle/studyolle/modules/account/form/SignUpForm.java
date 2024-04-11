package com.studyolle.studyolle.modules.account.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SignUpForm {

    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Length(min = 3, max = 20, message = "닉네임을 3자리에서 20자리 사이로 입력해 주세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9_-]{3,20}$", message = "닉네임은 3자리에서 20자리까지, 한글과 숫자만 가능합니다.")
    private String nickname;

    @Email(message = "이메일 형식이 올바르지 않습니다.")
    @NotBlank(message = "이메일을 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 50)
    private String password;

}
