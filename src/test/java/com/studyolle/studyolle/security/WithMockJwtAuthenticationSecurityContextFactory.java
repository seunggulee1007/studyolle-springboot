package com.studyolle.studyolle.security;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.Optional;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@RequiredArgsConstructor
public class WithMockJwtAuthenticationSecurityContextFactory implements WithSecurityContextFactory<WithMockJwtAuthentication> {


    private final AccountService accountService;

    private final AccountRepository accountRepository;

    @Override
    public SecurityContext createSecurityContext(WithMockJwtAuthentication annotation) {
        Optional<Account> account = accountRepository.findByEmail( "leesg107@naver.com" );
        if(!account.isPresent()) {
            String email = "leesg107@naver.com";
            String password = "123456789";
            SignUpForm signUpForm = new SignUpForm();
            signUpForm.setNickname( annotation.nickname() );
            signUpForm.setEmail( email );
            signUpForm.setPassword( password );
            accountService.processNewAccount( signUpForm );
        }
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        JwtAuthenticationToken authentication =
                new JwtAuthenticationToken(
                        new JwtAuthentication(annotation.id(), annotation.nickname()),
                        null,
                        createAuthorityList(annotation.role())
                );
        context.setAuthentication(authentication);
        return context;
    }

}