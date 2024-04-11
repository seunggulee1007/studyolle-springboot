package com.studyolle.studyolle.security;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public final class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final String secretKey;


    private final static long REFRESH_TOKEN_VALIDATION_SECOND = 1000L * 60 * 30; // 30분 동안 유효한 리프레시 토큰
    private final static long TOKEN_VALIDATION_SECOND = 1000L * 60 * 30;    // 5분 동안만 토큰 유효

    public Jwt(String issuer, String clientSecret, int expirySeconds) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expirySeconds = expirySeconds;
        this.secretKey = Base64.getEncoder().encodeToString(clientSecret.getBytes( StandardCharsets.UTF_8 ));
    }

    public String createAccessToken(Account account) {
        return createToken( account,  TOKEN_VALIDATION_SECOND);
    }

    public String createToken( Account account, long expireTime) {
        Claims claims = Jwts.claims().setSubject(account.getNickname());
        claims.put("roles", authorities(account.getRoles()));
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)  // 데이터
                .setIssuedAt(now)   // 토큰 발행 일자
                .setExpiration(new Date(now.getTime() + expireTime))  // 만료 시간 추가
                .signWith( SignatureAlgorithm.HS256, secretKey)      // 암호화 알고리즘, secret값 세팅
                .compact();
    }

    public String createRefreshToken(String nickname) {
        Claims claims = Jwts.claims().setSubject(nickname);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)  // 데이터
                .setIssuedAt(now)   // 토큰 발행 일자
                .setExpiration(new Date(now.getTime() + REFRESH_TOKEN_VALIDATION_SECOND))  // 일주일 동안 유효
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // Jwt 토큰에서 회원 구별 정보 추출
    public String getNickname(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Collection<? extends GrantedAuthority> authorities ( Set<AccountRole> role ) {
        return role.stream().map( r -> new SimpleGrantedAuthority( "ROLE_" + r.name() ) ).collect( Collectors.toSet() );
    }

}