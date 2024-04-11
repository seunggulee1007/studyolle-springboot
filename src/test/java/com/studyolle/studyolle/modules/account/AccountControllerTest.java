package com.studyolle.studyolle.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.infra.mail.EmailService;
import com.studyolle.studyolle.modules.account.form.SignUpForm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@MockMvcTest
class AccountControllerTest extends AbstractContainerBaseTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private AccountRepository accountRepository;
    @MockBean
    private EmailService emailService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired
    protected ObjectMapper objectMapper;

    @DisplayName( "인증 메일 확인 - 입력값 오류" )
    @Test
    void checkEmailToken_with_wrong_input() throws Exception {
        mockMvc.perform( get( "/api/check-email-token" )
                .param( "token", "sdfjslwfwef" )
                .param( "email", "email@email.com" ))
                .andExpect( status().isBadRequest() )
                .andDo( print() )
                ;
    }

    @DisplayName( "인증 메일 확인 - 입력값 정상" )
    @Test
    void checkEmailToken() throws Exception {
        Account account = Account.builder()
                        .email( "leesg107@naver.com" )
                        .password( "1234" )
                        .nickname( "seunggu" )
                        .build();
        Account newAccount = accountRepository.save( account );
        newAccount.generateEmailCheckToken();

        mockMvc.perform( get( "/api/check-email-token" )
                .param( "token", newAccount.getEmailCheckToken() )
                .param( "email", newAccount.getEmail() ))
                .andExpect( status().isOk() )
                .andDo( print() )
                ;
    }

    @DisplayName( "회원가입 처리 입력값 오류" )
    @Test
    void signUpSubmit_with_wrong_input() throws Exception {
        String email = "leesg107@naver.com";
        String password = "123456789";
        SignUpForm signUpForm = new SignUpForm();
        signUpForm.setNickname( "seunggu" );
        signUpForm.setEmail( email );
        signUpForm.setPassword( password );
        mockMvc.perform( post("/api/signUp")
                        .contentType( MediaType.APPLICATION_JSON )
                        .accept( MediaTypes.HAL_JSON )
                        .content( objectMapper.writeValueAsString( signUpForm ) )
            )
                .andExpect( status().isCreated() )
                .andDo( print() )
                .andExpect( jsonPath( "email" ).value( email ) )
                .andExpect( jsonPath( "_links.self" ).exists() )
                .andExpect( jsonPath( "_links.query-accounts" ).exists() )
                ;
        Account account = accountRepository.findByEmail( email ).orElseGet( Account::new );
        assertNotNull( account.getEmail() );
        assertNotEquals( account.getPassword(), password );
        assertTrue( accountRepository.existsByEmail( email ) );
        assertTrue(passwordEncoder.matches( password, account.getPassword() ));
        assertNotNull( account.getEmailCheckToken() );
        // then(emailService).should().sendEmail( any( EmailMessage.class ) );
    }

    @DisplayName( "계정이 없는 토큰 체크" )
    @Test
    void noAccountValidEmail() throws Exception {
        mockMvc.perform( get( "/api/check-email-token" )
                .param( "token", "asdfasdfasdf" )
                .param( "email", "seafsd@manva.com" ))
                .andExpect( status().isBadRequest() )
                .andDo( print() )
            ;
    }

}