package com.studyolle.studyolle.modules.tag;

import com.studyolle.studyolle.commons.BaseAnnotation;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.account.CurrentUser;
import com.studyolle.studyolle.modules.account.Tag;
import com.studyolle.studyolle.security.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.stream.Collectors;

@BaseAnnotation
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    private final AccountService accountService;

    @PostMapping("/tags/add")
    public ResponseEntity addTag( @CurrentUser JwtAuthentication authentication, @RequestBody TagForm tagForm) {
        String title = tagForm.getTagTitle();
        Tag tag = tagRepository.findByTitle(title).orElseGet( () -> tagRepository.save( Tag.builder()
                .title( title)
                .build() ) );
        accountService.addTag(authentication, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/tags")
    public ResponseEntity updateTags( @CurrentUser JwtAuthentication authentication) {
        Account account = accountService.getAccountByAuthentication(authentication);
        return ResponseEntity.ok().body( account.getTags().stream().map(Tag::getTitle).collect( Collectors.toSet()) );
    }
}
