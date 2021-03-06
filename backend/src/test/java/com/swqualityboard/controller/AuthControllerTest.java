package com.swqualityboard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swqualityboard.TestConfig;
import com.swqualityboard.configuration.security.SecurityConfig;
import com.swqualityboard.dto.auth.LoginDto;
import com.swqualityboard.dto.auth.TokenDto;
import com.swqualityboard.exception.user.UserNotFoundException;
import com.swqualityboard.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static com.swqualityboard.ApiDocumentUtils.getDocumentRequest;
import static com.swqualityboard.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

@ExtendWith(RestDocumentationExtension.class) // JUnit 5 ????????? ?????? ????????? ?????????
@Import({TestConfig.class})
@WebMvcTest(controllers = AuthController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)}
)
class AuthControllerTest {
    @MockBean
    private AuthService authService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    public void setup(WebApplicationContext webApplicationContext,
                      RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .apply(sharedHttpSession())
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @DisplayName("????????? - ?????? ????????? ????????? ??????????????? ????????? ??????")
    @Test
    public void ?????????() throws Exception {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("ssafy@gmail.com")
                .password("ssafy1234")
                .build();

        TokenDto tokenDto = TokenDto.builder().accessToken("eyJhbGciOiJIUzUxMiJ9.eyJlbWFpbCI6InNzYWZ5QGdtYWlsLmNvbSIsIm5pY2tuYW1lIjoiYWRtaW4xIiwicm9sZSI6IlJPTEVfRVhFQ1VUSVZFIiwiZXhwIjoxNjM1OTkyNjMxfQ.NRZ-TGDwHPtWILCXT_8WhD4WIAB_Ks1wafScDd8UMDJy93mJMo2rrE4yZkZuM2JEjukA2eMudthkrYi5EzF21A").build();

        //when
        doReturn(tokenDto).when(authService).authorize(any(LoginDto.class));

        //then
        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk()) // 200 isOk()
                .andDo(
                        document(
                                "authApi/authenticate/successful",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("????????? ??????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 50?????? ????????? ??????????????????. @*.com??? ????????? ???????????? ?????????.")),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("????????????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 20?????? ????????? ??????????????????."))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                                .description("?????? ?????? ??????"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                .description("?????? ??????"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("?????? ?????????"),
                                        fieldWithPath("result.accessToken").type(JsonFieldType.STRING)
                                                .description("?????? JWT"),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("api ?????? ??????")
                                )
                        ));
    }

    @DisplayName("????????? ?????? ???????????? ????????? - ?????? ?????? ????????? ????????? ??????")
    @Test
    public void ?????????_??????_????????????() throws Exception {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("ssafy@gmail.com")
                .password("ssafy12345")
                .build();

        //when
        doThrow(new BadCredentialsException("?????? ????????? ?????????????????????.")).when(authService).authorize(any(LoginDto.class));

        //then
        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized()) // 401
                .andDo(
                        document(
                                "authApi/authenticate/failure_credential",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("????????? ??????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 50?????? ????????? ??????????????????. @*.com??? ????????? ???????????? ?????????.")),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("????????????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 20?????? ????????? ??????????????????."))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                                .description("?????? ?????? ??????"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                .description("?????? ??????"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("?????? ?????????"),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("api ?????? ??????")
                                )
                        ));
    }

    @DisplayName("????????? ?????? ???????????? ?????? ?????? - ???????????? ?????? ????????? ????????? ??????")
    @Test
    public void ?????????_??????_????????????_??????_??????() throws Exception {
        //given
        LoginDto loginDto = LoginDto.builder()
                .email("ssafyas@gmail.com")
                .password("ssafy12345")
                .build();

        //when
        doThrow(new UserNotFoundException("???????????? ???????????? ?????? ????????? ???????????? ????????????.")).when(authService).authorize(any(LoginDto.class));

        //then
        mockMvc.perform(post("/api/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDto)).accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound()) // 404
                .andDo(
                        document(
                                "authApi/authenticate/failure_not_found",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("email").type(JsonFieldType.STRING)
                                                .description("????????? ??????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 50?????? ????????? ??????????????????. @*.com??? ????????? ???????????? ?????????.")),
                                        fieldWithPath("password").type(JsonFieldType.STRING)
                                                .description("????????????")
                                                .attributes(key("constraint")
                                                        .value("?????? 3??????, ?????? 20?????? ????????? ??????????????????."))
                                ),
                                responseFields(
                                        fieldWithPath("isSuccess").type(JsonFieldType.BOOLEAN)
                                                .description("?????? ?????? ??????"),
                                        fieldWithPath("statusCode").type(JsonFieldType.NUMBER)
                                                .description("?????? ??????"),
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description("?????? ?????????"),
                                        fieldWithPath("timestamp").type(JsonFieldType.STRING)
                                                .description("api ?????? ??????")
                                )
                        ));
    }
}
