package com.studyolle.studyolle.commons;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("my-app")
public class MyAppProperties {

    private String adminUsername;

    private String adminPassword;

    private String adminEmail;

    private String userUsername;

    private String userUserPassword;

    private String clientId;

    private String clientSecret;

}
