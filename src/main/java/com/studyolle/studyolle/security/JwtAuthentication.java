package com.studyolle.studyolle.security;

import static com.google.common.base.Preconditions.checkNotNull;

public class JwtAuthentication {

    public final Long id;

    public final String nickname;

    JwtAuthentication(Long id, String nickname) {
        checkNotNull(id, "id must be provided");
        checkNotNull(nickname, "nickname must be provided");

        this.id = id;
        this.nickname = nickname;
    }

}