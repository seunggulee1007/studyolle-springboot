package com.studyolle.studyolle.security;

import com.studyolle.studyolle.modules.account.*;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import static org.apache.commons.lang3.ClassUtils.isAssignable;
import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;

@RequiredArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtAuthenticationToken authenticationToken = (JwtAuthenticationToken) authentication;
        return processUserAuthentication(
                Email.of(String.valueOf(authenticationToken.getPrincipal())),
                authenticationToken.getCredentials()
        );
    }

    private Authentication processUserAuthentication(Email email, String password) {
        try {
            Account account = accountService.login(email, password);
            JwtAuthenticationToken authenticated =
                    new JwtAuthenticationToken(
                            new JwtAuthentication(account.getId(), account.getNickname()),
                            null,
                            createAuthorityList( Role.USER.value())
                    );
            authenticated.setDetails(account);
            return authenticated;
        } catch ( NotFoundException e) {
            throw new UsernameNotFoundException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException(e.getMessage());
        } catch (DataAccessException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return isAssignable(JwtAuthenticationToken.class, authentication);
    }

}