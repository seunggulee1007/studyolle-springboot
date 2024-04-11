package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.commons.BaseController;
import com.studyolle.studyolle.commons.ErrorsResource;
import com.studyolle.studyolle.security.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/study")
@RequiredArgsConstructor
public class StudyController extends BaseController {

    private final AccountService accountService;

    private final ErrorsResource errorsResource;

    private final StudyService studyService;

    private final StudyFormValidator studyFormValidator;

    private final StudyRepository studyRepository;

    @InitBinder("studyForm")
    public void initBinder (WebDataBinder webDataBinder) {
        webDataBinder.addValidators( studyFormValidator );
    }

    @GetMapping("/new-study")
    public ResponseEntity newStudyForm( @AuthenticationPrincipal JwtAuthentication authentication ) {
        StudyForm studyForm = new StudyForm();
        studyForm.setAccount( accountService.getAccountByAuthentication( authentication ) );
        return ResponseEntity.ok(studyForm);
    }

    @PostMapping("/new-study")
    public ResponseEntity createNewStudy( @AuthenticationPrincipal JwtAuthentication authentication, @Valid StudyForm studyForm, Errors errors ) {
        if(errors.hasErrors()) {
            return badRequest( errorsResource.createErrorResource( errors ) );
        }

        Study newStudy = studyService.createNewStudy(studyForm, accountService.getAccountByAuthentication( authentication ));
        StudyDto studyDto = new StudyDto(newStudy);
        studyDto.setRedirectUrl( "/study/" + URLEncoder.encode( newStudy.getPath(), StandardCharsets.UTF_8 ) );
        return ResponseEntity.ok(newStudy);
    }

    @GetMapping("/{path}")
    public void viewStudy(@AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path) {
        StudyDto stduyDto = new StudyDto(studyService.getStudy(path));
        stduyDto.setAccount( accountService.getAccountByAuthentication( authentication ) );
    }

    @GetMapping("/{path}/members")
    public void viewStudyMembers( @AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path ) {
        StudyDto stduyDto = new StudyDto(studyService.getStudy(path));
        stduyDto.setAccount( accountService.getAccountByAuthentication( authentication ) );
    }


}
