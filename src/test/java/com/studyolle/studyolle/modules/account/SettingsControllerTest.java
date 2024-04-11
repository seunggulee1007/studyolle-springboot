package com.studyolle.studyolle.modules.account;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyolle.studyolle.infra.AbstractContainerBaseTest;
import com.studyolle.studyolle.infra.MockMvcTest;
import com.studyolle.studyolle.security.WithMockJwtAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class SettingsControllerTest extends AbstractContainerBaseTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName( "프로필 수정하기 - 입력값 정상" )
    @Test
    @WithMockJwtAuthentication
    void updateProfile() throws Exception {
        String bio = "짧은 소개를 수정하는 경우";
        mockMvc.perform( post("/api/settings/profile")
                .param("bio", bio ))
                .andDo( print() )
                .andExpect( status().isOk() )
                .andExpect( header().string( HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8" ) )
                .andExpect( jsonPath( "id" ).exists() )
                ;
        Account tester = accountRepository.findByNickname( "seunggu" ).orElseThrow( NoMemberException::new );
        assertEquals( bio,  tester.getBio() );
    }

    @DisplayName( "프로필 수정하기 - 입력값 오류" )
    @Test
    @WithMockJwtAuthentication
    void updateProfile_error() throws Exception {
        String bio = "짧은 소개를 수정하는 경우가 아니라 엄청 길고 긴 소개를 수정하는 경우에는 에러가 발생하여야 합니다. 인정? 어 인정";
        mockMvc.perform( post("/api/settings/profile")
                        .param("bio", bio ))
                .andDo( print() )
                .andExpect( status().isBadRequest() )
                .andExpect( header().string( HttpHeaders.CONTENT_TYPE, "application/hal+json;charset=UTF-8" ) )
        ;
        Account tester = accountRepository.findByNickname( "seunggu" ).orElseThrow( NoMemberException::new );
        assertNotEquals( bio, tester.getBio() );
    }

    @DisplayName( "비밀번호 변경하기 - 정상" )
    @Test
    @WithMockJwtAuthentication
    void updatePassword() throws Exception {
        PasswordForm passwordForm = PasswordForm.builder()
                        .newPassword( "qlalfqjsgh1!" )
                        .newPasswordConfirm( "qlalfqjsgh1!" )
                                .build();
        mockMvc.perform( put( "/api/settings/password" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString( passwordForm ) )
        )
                .andDo( print() )
                .andExpect( status().isOk() )
        ;
    }

    @DisplayName( "비밀번호 변경하기 - 입력값 오류" )
    @Test
    @WithMockJwtAuthentication
    void updatePassword_error() throws Exception {
        PasswordForm passwordForm = PasswordForm.builder()
                .newPassword( "qlalfqjsgh" )
                .newPasswordConfirm( "qlalfqjsgh1!" )
                .build();
        mockMvc.perform( put( "/api/settings/password" )
                .contentType( MediaType.APPLICATION_JSON )
                .content( objectMapper.writeValueAsString( passwordForm ) )
        )
                .andExpect( status().isBadRequest() )
                .andDo( print() )
                .andExpect( jsonPath( "errors" ).exists() )
                ;
    }

}