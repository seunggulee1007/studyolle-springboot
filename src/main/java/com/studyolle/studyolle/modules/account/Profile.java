package com.studyolle.studyolle.modules.account;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public  class Profile {

    @Length(max = 35, message = "짧은 소개는 35자까지 입력하실 수 있습니다.")
    private String bio;

    @Length(max = 50, message = "url은 50자까지 입력하실 수 있습니다.")
    private String url;

    @Length(max = 50, message = "url은 50자까지 입력하실 수 있습니다.")
    private String occupation;

    @Length(max = 50, message = "url은 50자까지 입력하실 수 있습니다.")
    private String location;

    private String profileImage;

}
