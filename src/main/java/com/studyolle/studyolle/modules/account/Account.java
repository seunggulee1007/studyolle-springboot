package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.modules.zone.Zone;
import com.studyolle.studyolle.security.Jwt;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static java.time.LocalDateTime.now;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    private boolean emailVerified;

    private String emailCheckToken;

    private LocalDateTime joinedAt;

    private String bio;

    private String url;

    private String occupation;

    private String location;    // varchar(255)

    @Lob @Basic(fetch = FetchType.LAZY)
    private String profileImage;

    private boolean studyCreatedByEmail;

    private boolean studyCreatedByWeb;

    private boolean studyEnrollmentResultByEmail;

    private boolean studyEnrollmentResultByWeb;

    private boolean studyUpdatedByEmail;

    private boolean studyUpdatedByWeb;

    private int loginCount;

    private LocalDateTime lastLoginAt;

    private LocalDateTime emailCheckTokenGeneratedAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<AccountRole> roles;

    @ManyToMany
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    @Builder.Default
    private Set<Zone> zones = new HashSet<>();

    @Transient
    private Set<String> tagList;

    public void generateEmailCheckToken() {
        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }

    public void completeSignUp () {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) { return this.emailCheckToken.equals( token );}

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore( LocalDateTime.now().minusHours( 1 ) );
    }

    public void login( PasswordEncoder passwordEncoder, String credentials) {
        if (!passwordEncoder.matches(credentials, password)) {
            throw new IllegalArgumentException("Bad credential");
        }
    }

    public void afterLoginSuccess () {
        loginCount++;
        lastLoginAt = now();
    }

    public String newAccessJwt ( Jwt jwt ) {
        return jwt.createAccessToken(this);
    }

    public String newRefreshJwt ( Jwt jwt ) {
        return jwt.createRefreshToken( this.nickname );
    }

    @Override
    public boolean equals ( Object o ) {
        if ( this == o ) return true;
        if ( o == null || Hibernate.getClass( this ) != Hibernate.getClass( o ) ) return false;
        Account account = ( Account ) o;
        return Objects.equals( id, account.id );
    }

    @Override
    public int hashCode () {
        return 0;
    }

}


