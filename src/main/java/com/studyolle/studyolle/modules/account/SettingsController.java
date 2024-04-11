package com.studyolle.studyolle.modules.account;

import com.studyolle.studyolle.commons.BaseAnnotation;
import com.studyolle.studyolle.commons.BaseController;
import com.studyolle.studyolle.commons.ErrorsResource;
import com.studyolle.studyolle.modules.tag.TagForm;
import com.studyolle.studyolle.security.JwtAuthentication;
import com.studyolle.studyolle.modules.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@BaseAnnotation
@RequiredArgsConstructor
@RequestMapping("/api/settings")
public class SettingsController extends BaseController {


    private final CustomEntityModel customEntityModel;
    private final ErrorsResource errorsResource;
    private final AccountService accountService;

    @InitBinder("passwordForm")
    public void initBinderPasswordForm( WebDataBinder webDataBinder ) {
        webDataBinder.addValidators( new PasswordFormValidator() );
    }

    @PostMapping("/profile")
    public ResponseEntity updateProfile( @CurrentUser JwtAuthentication authentication, @Valid Profile profile, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource(errors) );
        }
        Account account = accountService.updateProfile( authentication, profile );
        EntityModel<Account> entityModel = customEntityModel.createEntityModel( account,
                linkTo(methodOn(SettingsController.class).updateProfile( authentication, profile, errors )).withSelfRel(),
                linkTo(methodOn(SettingsController.class).updatePassword( authentication, null, errors ) ).withRel( "update-password" )
                );
        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/password")
    public ResponseEntity updatePassword( @CurrentUser JwtAuthentication authentication, @RequestBody @Valid PasswordForm passwordForm, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource( errors ) );
        }

        Account account = accountService.updatePassword( authentication, passwordForm );
        EntityModel<Account> entityModel = customEntityModel.createEntityModel( account,
                linkTo(methodOn(SettingsController.class).updatePassword( authentication, passwordForm, errors ) ).withSelfRel(),
                linkTo(methodOn(SettingsController.class).updateProfile( authentication, null, errors )).withRel("update-profile")
        );

        return ResponseEntity.ok(entityModel);
    }

    @PutMapping("/notification")
    public ResponseEntity updateNotification( @CurrentUser JwtAuthentication authentication, @Valid Notifications notifications, Errors errors) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource( errors ) );
        }
        accountService.updateNotification(authentication, notifications);
        EntityModel<Notifications> entityModel = customEntityModel.createEntityModel( notifications,
                linkTo(methodOn( SettingsController.class ).updateNotification( authentication, notifications, errors )).withSelfRel());
        return ResponseEntity.ok( entityModel );
    }



}
