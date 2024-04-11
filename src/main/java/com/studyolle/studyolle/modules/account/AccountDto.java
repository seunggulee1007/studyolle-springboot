package com.studyolle.studyolle.modules.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static org.springframework.beans.BeanUtils.copyProperties;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDto {

    private Long id;

    private String email;

    private String nickname;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;

    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    private TokenDto tokenDto;

    public AccountDto(Account account) {
        copyProperties(account, this);
    }

}
