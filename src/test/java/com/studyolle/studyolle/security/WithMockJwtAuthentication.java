package com.studyolle.studyolle.security;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockJwtAuthenticationSecurityContextFactory.class)
public @interface WithMockJwtAuthentication {

    long id () default 1L;

    String nickname () default "seunggu";

    String role () default "ROLE_USER";

}