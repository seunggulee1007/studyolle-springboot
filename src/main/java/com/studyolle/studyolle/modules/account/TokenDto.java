package com.studyolle.studyolle.modules.account;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String resultMsg = "유효하지 않은 토큰입니다.";
}
