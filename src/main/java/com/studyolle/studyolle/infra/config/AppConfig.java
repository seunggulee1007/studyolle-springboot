package com.studyolle.studyolle.infra.config;

import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import com.studyolle.studyolle.commons.MyAppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile( "local" )
@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final MyAppProperties myAppProperties;

    @Bean
    public ApplicationRunner applicationRunner () {
        return new ApplicationRunner() {
            @Autowired
            AccountService accountService;

            @Override
            public void run ( ApplicationArguments args ) throws Exception {
                SignUpForm signUpForm = SignUpForm.builder()
                                .email( myAppProperties.getAdminEmail() )
                                .nickname( myAppProperties.getAdminUsername() )
                                .password( myAppProperties.getAdminPassword() )
                            .build();
                accountService.processNewAccount( signUpForm  );
            }
        };
    }
}
