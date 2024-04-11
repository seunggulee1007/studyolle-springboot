package com.studyolle.studyolle.modules.main;

import com.studyolle.studyolle.commons.BaseAnnotation;
import com.studyolle.studyolle.commons.BaseController;
import com.studyolle.studyolle.commons.ErrorsResource;
import com.studyolle.studyolle.modules.account.*;
import com.studyolle.studyolle.modules.account.form.LoginForm;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyRepository;
import com.studyolle.studyolle.security.Jwt;
import com.studyolle.studyolle.security.JwtAuthenticationToken;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@BaseAnnotation
@RequiredArgsConstructor
@RequestMapping("/api")
public class MainController extends BaseController {

    private final Jwt jwt;
    private final AuthenticationManager authenticationManager;
    private final ErrorsResource errorsResource;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomEntityModel customEntityModel;
    private final StudyRepository studyRepository;

    @PostMapping("/login")
    public ResponseEntity login( @Valid @RequestBody LoginForm loginForm, Errors errors ) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource(errors) );
        }
        Account account = accountRepository.findByNickname(loginForm.getNickname()).orElseThrow(()-> new AccountException( "존재하지 않는 계정입니다." ) );

        if(!passwordEncoder.matches( loginForm.getPassword(), account.getPassword() )) {
            throw new AccountException( "비밀번호가 일치하지 않습니다." );
        }
        authenticationManager.authenticate(
                new JwtAuthenticationToken(account.getEmail(), loginForm.getPassword())
        );
        final String token = account.newAccessJwt(jwt);
        final String refreshToken = account.newRefreshJwt(jwt);

        AccountDto accountDto = new AccountDto(account);
        TokenDto tokenDto = TokenDto.builder()
                        .accessToken( token )
                        .refreshToken( refreshToken )
                        .build();
        accountDto.setTokenDto( tokenDto );
        EntityModel<Map> entityModel = customEntityModel.createEntityModel( accountDto,
                linkTo( MainController.class ).withSelfRel());
        return ResponseEntity.ok(entityModel);
    }

    @GetMapping("/access-token/refresh-token/{refreshToken}")
    public ResponseEntity rePublishAccessTokenByRefreshToken(@PathVariable String refreshToken) {
        TokenDto tokenDto = new TokenDto();
        if(jwt.validateToken( refreshToken )) {
            Optional<Account> account = accountRepository.findByNickname( jwt.getNickname( refreshToken ) );
            if(account.isPresent()) {
                Account newAccount = account.get();
                final String token = newAccount.newAccessJwt(
                        jwt
                );
                final String newRefreshToken = newAccount.newRefreshJwt(
                        jwt
                );
                tokenDto.setAccessToken( token );
                tokenDto.setRefreshToken( newRefreshToken );
                tokenDto.setResultMsg( "토큰이 발급되었습니다." );
                return ResponseEntity.ok(tokenDto);
            } else {
                return ResponseEntity.badRequest().body( tokenDto );
            }
        } else {
            return ResponseEntity.badRequest().body( tokenDto );
        }
    }

    @GetMapping("/search/study")
    public ResponseEntity searchStudy(String keyword) {
        List<Study> studyList = studyRepository.findByKeywordByTransform(keyword);
        System.err.println(studyList);
        return ResponseEntity.ok(studyList);
    }


}
