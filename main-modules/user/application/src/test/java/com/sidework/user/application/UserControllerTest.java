package com.sidework.user.application;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.user.application.port.in.SignUpCommand;
import com.sidework.user.application.adapter.UserController;
import com.sidework.user.application.port.in.UserCommandUseCase;
import com.sidework.user.application.port.in.UserQueryUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = UserTestApplication.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserCommandUseCase userCommandUseCase;

    @MockitoBean
    private UserQueryUseCase userQueryUseCase;

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

    private SignUpCommand createCommand(){
        return new SignUpCommand(
                "test@test.com",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }

    private SignUpCommand createNullExistCommand(){
        return new SignUpCommand(
                null,
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }

    private SignUpCommand createNotEmailCommand(){
        return new SignUpCommand(
                "email",
                "password123!",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }

    private SignUpCommand createShortPasswordCommand(){
        return new SignUpCommand(
                "test1@test.com",
                "passwor",
                "홍길동",
                "길동",
                20,
                "010-1234-5678"
        );
    }
}
