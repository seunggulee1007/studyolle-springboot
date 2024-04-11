package com.studyolle.studyolle.modules.study;

import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.commons.BaseController;
import com.studyolle.studyolle.security.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/study/{path}/settings")
@RequiredArgsConstructor
public class StudySettingsController extends BaseController {

    private final StudyService studyService;
    private final AccountService accountService;

    @GetMapping("/description")
    public void viewStudySetting( @AuthenticationPrincipal JwtAuthentication authentication, @PathVariable String path ) {
        Account account = accountService.getAccountByAuthentication( authentication );
        Study study = studyService.getStudyToUpdate(account, path);
        StudyDescriptionForm studyDescriptionForm = new StudyDescriptionForm(study);
    }
}
