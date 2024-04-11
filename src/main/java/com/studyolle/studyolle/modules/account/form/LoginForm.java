package com.studyolle.studyolle.modules.account.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginForm {

    @NotBlank(message = "닉네임을 입력해 주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    @Length(min = 8, max = 50, message = "비밀번호는 8자에서 50자 사이로 입력해 주세요.")
    private String password;

}
