package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.commons.ErrorsResource;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import com.studyolle.studyolle.modules.account.form.SignUpFormValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AccountController {

    private final SignUpFormValidator signUpFormValidator;
    private final AccountService accountService;
    private final ErrorsResource errorsResource;
    private final CustomEntityModel customEntityModel;
    private final AccountRepository accountRepository;

    @InitBinder("signUpForm")
    public void initBinder( WebDataBinder webDataBinder) {
        webDataBinder.addValidators( signUpFormValidator );
    }

    @PostMapping("/signUp")
    public ResponseEntity signUpSubmit( @RequestBody @Valid SignUpForm signUpForm, Errors errors ) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource(errors) );
        }
        Account account = accountService.processNewAccount( signUpForm );
        WebMvcLinkBuilder selfLinkBuilder = linkTo(AccountController.class).slash( account.getId() );
        URI createdUri = linkTo( AccountController.class ).slash( account.getId() ).toUri();

        EntityModel<Account> entityModel = customEntityModel.createEntityModel( account,
                linkTo( AccountController.class ).withRel( "query-accounts" ),
                selfLinkBuilder.withSelfRel() );
        return ResponseEntity.created( createdUri ).body( entityModel );
    }

    @GetMapping("/check-email-token")
    public ResponseEntity checkEmailToken(String token, String email) {
        return accountService.checkEmailToken( token, email );
    }

    @GetMapping("/resend-confirm-email")
    public ResponseEntity resendConfirmEmail(@CurrentUser Account account) {
        if( !account.canSendConfirmEmail() ) {

        }
        accountService.sendSignUpConfirmEmail( account );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile/{nickname}")
    public ResponseEntity viewProfile(@PathVariable String nickname, @CurrentUser Account account) {
        // Account byNickname = accountRepository.findByNickname(nickname).orElseThrow( JwtAccessDeniedHandler::new);
        return ResponseEntity.ok("dd");
    }

    private ResponseEntity badRequest ( EntityModel<Errors> errorResource ) {
        return ResponseEntity.badRequest().body( errorResource );
    }

}
