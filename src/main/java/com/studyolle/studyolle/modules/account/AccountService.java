package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.modules.account.form.SignUpForm;
import com.studyolle.studyolle.commons.AppProperties;
import com.studyolle.studyolle.infra.mail.EmailMessage;
import com.studyolle.studyolle.infra.mail.EmailService;
import com.studyolle.studyolle.security.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.springframework.beans.BeanUtils.copyProperties;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final TemplateEngine templateEngine;
    private final AppProperties appProperties;

    public Account processNewAccount( SignUpForm signUpForm) {
        Account newAccount = saveNewAccount( signUpForm );
        newAccount.generateEmailCheckToken();
        sendSignUpConfirmEmail(newAccount);
        return newAccount;
    }

    private Account saveNewAccount( SignUpForm signUpForm) {
        Account account = Account.builder()
                .email( signUpForm.getEmail() )
                .nickname( signUpForm.getNickname() )
                .password( passwordEncoder.encode( signUpForm.getPassword() ) )
                .studyEnrollmentResultByWeb( true )
                .studyCreatedByWeb( true )
                .studyUpdatedByWeb( true )
                .roles(Set.of( AccountRole.ADMIN, AccountRole.USER ))
                .build();
        return accountRepository.save( account );
    }

    public void sendSignUpConfirmEmail ( Account newAccount ) {
        Context context = new Context();
        context.setVariable( "link", "/check-email-token?token=" + newAccount.getEmailCheckToken() + "&email=" + newAccount.getEmail() );
        context.setVariable( "nickname", newAccount.getNickname() );
        context.setVariable( "linkName", "이메일 인증하기" );
        context.setVariable( "message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요." );
        context.setVariable( "host", appProperties.getHost() );
        String message = templateEngine.process( "mail/simple-link", context );
        EmailMessage emailMessage = EmailMessage.builder()
                .to( newAccount.getEmail() )
                .subject( "스터디올래, 회원 가입 인증" )
                .message( message )
                .build();

        emailService.sendEmail( emailMessage );
    }

    public void sendLoginLink(Account account) {
        Context context = new Context();
        context.setVariable( "link", "/login-by-email?token=" + account.getEmailCheckToken() + "&email=" + account.getEmail() );
        context.setVariable( "nickname", account.getNickname() );
        context.setVariable( "linkName", "이메일 인증하기" );
        context.setVariable( "message", "스터디올래 서비스를 사용하려면 링크를 클릭하세요." );
        context.setVariable( "host", appProperties.getHost() );
        String message = templateEngine.process( "mail/simple-link", context );
        EmailMessage emailMessage = EmailMessage.builder()
                .to( account.getEmail() )
                .subject( "스터디올래, 로그인 링크" )
                .message(  message )
                .build();

        emailService.sendEmail( emailMessage );
    }

    public ResponseEntity<String> checkEmailToken( String token, String email) {
        Account account = accountRepository.findByEmail( email ).orElseThrow(()-> new AccountException("해당 이메일과 일치하는 계정이 없습니다."));
        if(!account.getEmailCheckToken().equals( token )) {
            return ResponseEntity.badRequest().body( token );
        }
        account.completeSignUp();
        return ResponseEntity.ok(email);
    }

    public Account login(Email email, String password) {
        checkNotNull(password, "password must be provided");
        Account account = accountRepository.findByEmail( email.getAddress() ).orElseThrow(() -> new NotFoundException("Could not found user for " + email));
        account.login(passwordEncoder, password);
        account.afterLoginSuccess();
        return account;
    }

    public Account updateProfile( JwtAuthentication authentication, Profile profile) {
        Account account = getAccountByAuthentication( authentication );
        copyProperties(profile, account);
        return account;
        // TODO 프로필 이미지
        // TODO 문제가 하나 더 남았습니다.
    }

    public Account updatePassword( JwtAuthentication authentication, PasswordForm passwordForm ) {
        Account account = getAccountByAuthentication( authentication );
        account.setPassword( passwordEncoder.encode( passwordForm.getNewPassword() ) );
        return account;
    }

    public void updateNotification ( JwtAuthentication authentication, Notifications notifications ) {
        Account account = getAccountByAuthentication( authentication );
        copyProperties( notifications, account );
    }

    public Account getAccountByAuthentication(JwtAuthentication authentication) {
        return accountRepository.findByNickname( authentication.nickname ).orElseThrow(NoMemberException::new);
    }


    public void addTag ( JwtAuthentication authentication, Tag tag ) {
        Account account = getAccountByAuthentication( authentication );
        account.setTags( Set.of(tag) );
    }

}
