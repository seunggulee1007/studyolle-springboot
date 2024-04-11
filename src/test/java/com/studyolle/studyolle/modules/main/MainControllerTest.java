package com.studyolle.studyolle.modules.main;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.modules.account.Account;
import com.studyolle.studyolle.modules.account.AccountRepository;
import com.studyolle.studyolle.modules.account.AccountService;
import com.studyolle.studyolle.modules.account.Tag;
import com.studyolle.studyolle.modules.account.form.LoginForm;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import com.studyolle.studyolle.modules.study.Study;
import com.studyolle.studyolle.modules.study.StudyForm;
import com.studyolle.studyolle.modules.study.StudyRepository;
import com.studyolle.studyolle.modules.study.StudyService;
import com.studyolle.studyolle.modules.tag.TagRepository;
import com.studyolle.studyolle.security.Jwt;
import com.studyolle.studyolle.security.JwtAuthentication;
import com.studyolle.studyolle.security.WithMockJwtAuthentication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
@Transactional
class MainControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;
    @Autowired
    StudyService studyService;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    AccountRepository accountRepository;

    @MockBean
    JavaMailSender javaMailSender;

    @MockBean
    Jwt jwt;

    @DisplayName( "이메일로 로그인 성공" )
    @Test
    void login_with_email() throws Exception {
        LoginForm loginForm = LoginForm.builder()
                        .nickname( "seunggu" )
                        .password( "123456789" )
                                .build();
        mockMvc.perform( post("/api/login")
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString( loginForm ) ))
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "id" ).exists() )
                .andExpect( jsonPath( "email").exists() )
                .andDo( print() )
            ;
    }

    @DisplayName( "로그인 실패" )
    @Test
    void login_fail_with_email() throws Exception {

        LoginForm loginForm = LoginForm.builder()
                .nickname( "seunggu" )
                .password( "1234567" )
                .build();
        mockMvc.perform( post("/api/login")
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( loginForm ) ))
                .andExpect( status().is4xxClientError() )
                .andDo( print() )
        ;
    }

    @DisplayName( "스터디 조회 시 transform작동하는 지 실험" )
    @Test
    @WithMockJwtAuthentication
    void findStudyListByTransform() throws Exception {
        Optional<Account> account = accountRepository.findByEmail( "leesg107@naver.com" );
        if(account.isPresent()) {
            System.err.println("존재하는데 ???");
        }else{
            System.err.println("설마 존재하지 않아 ???");
        }
        Tag tag = Tag.builder().title( "jpa" ).build();
        tagRepository.save( tag );
        Tag tag2 = Tag.builder().title( "spring" ).build();
        tagRepository.save( tag2 );
        StudyForm studyForm = StudyForm.builder()
                .title( "testTitle1" )
                .path( "test" )
                .shortDescription( "test" )
                .build();
        Study newStudy = studyService.createNewStudy( studyForm, account.get() );
        Tag jpa = tagRepository.findByTitle( "jpa" ).orElseGet( Tag::new );
        Tag spring = tagRepository.findByTitle( "spring" ).orElseGet( Tag::new );
        newStudy.getTags().add( jpa );
        newStudy.getTags().add( spring);
        newStudy.publish();
        studyForm = StudyForm.builder()
                .title( "testTitle2" )
                .path( "test2" )
                .shortDescription( "test2" )
                .build();
        newStudy = studyService.createNewStudy( studyForm, account.get() );
        newStudy.getTags().add( jpa );
        newStudy.publish();
        mockMvc.perform( get( "/api/search/study" ).param( "keyword", "test" ) )
                .andDo( print() )
                .andExpect( status().isOk() );
    }

}