package com.sidework.user.application;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.user.application.port.in.GithubInfoResponse;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.adapter.UserController;
import com.sidework.user.application.port.in.UserCommandUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = UserTestApplication.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserCommandUseCase userCommandUseCase;

    @MockitoBean
    private UserQueryUseCase userQueryUseCase;

    private final AuthenticatedUserDetails authenticatedUserDetails = new AuthenticatedUserDetails(
            1L, "test@test.com", "테스터", "password");


    @Test
    void 회원가입_요청시_성공하면_201을_반환한다() throws Exception {
        // given
        SignUpCommand command = createCommand();
        doNothing().when(userCommandUseCase).signUp(command);

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true));

        verify(userCommandUseCase).signUp(any());
    }

    @Test
    void 회원가입_요청시_어느_하나의_값이라도_Null이면_400을_반환한다() throws Exception {
        // given
        SignUpCommand command = createNullExistCommand();

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_요청시_이메일_형식이_잘못되면_400을_반환한다() throws Exception {
        // given
        SignUpCommand command = createNotEmailCommand();

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_요청시_비밀번호_길이가_8보다_짧으면_400을_반환한다() throws Exception {
        // given
        SignUpCommand command = createShortPasswordCommand();

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_요청시_나이가_19보다_작으면_400을_반환한다() throws Exception {
        // given
        SignUpCommand command = createAgeLowerCommand();

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 회원가입_요청시_나이가_100보다_크면_400을_반환한다() throws Exception {
        // given
        SignUpCommand command = createAgeHigherCommand();

        // when & then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 이메일_중복_확인시_중복이면_true를_반환한다() throws Exception {
        // given
        String email = "test@test.com";
        when(userQueryUseCase.checkEmailExists(email)).thenReturn(true);

        // when & then
        mockMvc.perform(get("/api/v1/users/email")
                        .param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isExist").value(true));

        verify(userQueryUseCase).checkEmailExists(email);
    }

    @Test
    void 이메일_중복_확인시_중복이_아니면_false를_반환한다() throws Exception {
        // given
        String email = "new@test.com";
        when(userQueryUseCase.checkEmailExists(email)).thenReturn(false);

        // when & then
        mockMvc.perform(get("/api/v1/users/email")
                        .param("email", email))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.isExist").value(false));

        verify(userQueryUseCase).checkEmailExists(email);
    }

    @Test
    void 깃허브_정보_확인_성공시_200과_깃허브정보를_반환한다() throws Exception {
        // given
        Long userId = 1L;
        when(userQueryUseCase.queryGithubInformation(userId)).thenReturn(new GithubInfoResponse(1L, "test" ,"accesstoken"));

        // when & then
        mockMvc.perform(get("/api/v1/users/github")
                        .with(user(authenticatedUserDetails))
                        .header("x-user-id", userId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.githubId").value(1))
                .andExpect(jsonPath("$.githubLoginName").value("test"))
                .andExpect(jsonPath("$.githubAccessToken").value("accesstoken"));

        verify(userQueryUseCase).queryGithubInformation(userId);
    }


    @Test
    void 깃허브_정보_조회시_유저정보가_없으면_401을_반환한다() throws Exception {
        mockMvc.perform(get("/api/v1/users/github").with(anonymous()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    private SignUpCommand createCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createNullExistCommand(){
        return new SignUpCommand(
                null,
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createNotEmailCommand(){
        return new SignUpCommand(
                "email",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createAgeLowerCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                18,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createAgeHigherCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                101,
                "010-1234-5678",
                1L
        );
    }

    private SignUpCommand createShortPasswordCommand(){
        return new SignUpCommand(
                "test1@test.com",
                "passwor",
                "홍길동",
                "길동",
                20,
                "010-1234-5678",
                1L
        );
    }
}
