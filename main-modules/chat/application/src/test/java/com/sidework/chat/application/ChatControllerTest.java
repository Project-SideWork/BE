package com.sidework.chat.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.chat.application.adapter.ChatController;
import com.sidework.chat.application.adapter.ChatRecord;
import com.sidework.chat.application.port.in.ExistChatCommand;
import com.sidework.chat.application.port.in.NewChatCommand;
import com.sidework.chat.application.port.in.ChatCommandUseCase;
import com.sidework.chat.application.port.in.ChatMessageQueryResult;
import com.sidework.chat.application.port.in.ChatQueryUseCase;
import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.event.sse.port.in.SseSubscribeUseCase;
import com.sidework.common.exception.ForbiddenAccessException;
import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.response.exception.ExceptionAdvice;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatController.class)
@ContextConfiguration(classes = ChatTestApplication.class)
@Import(ExceptionAdvice.class)
public class ChatControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ChatCommandUseCase chatCommandService;

    @MockitoBean
    private ChatQueryUseCase chatQueryService;

    @MockitoBean
    private SseSubscribeUseCase sseSubscribeUseCase;

    @Test
    void SSE_구독_성공시_200을_반환한다() throws Exception {
        SseEmitter emitter = new SseEmitter();

        when(sseSubscribeUseCase.subscribeChat(anyLong()))
                .thenReturn(emitter);

        mockMvc.perform(get("/api/v1/chats/subscribe/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void SSE_구독_시_인증정보가_없으면_401을_반환한다() throws Exception {
        SseEmitter emitter = new SseEmitter();

        when(sseSubscribeUseCase.subscribeChat(anyLong()))
                .thenReturn(emitter);

        mockMvc.perform(get("/api/v1/chats/subscribe/{chatRoomId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    void SSE_구독_시_존재하지_않는_채팅방의_ID로_구독_시도하면_400을_반환한다() throws Exception {
        doThrow(new InvalidCommandException(1L + "은 존재하지 않는 채팅방 ID 입니다."))
                .when(chatQueryService)
                .checkChatUserValidation(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/chats/subscribe/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void SSE_구독_시_내가_속하지_않은_채팅방의_ID로_구독_시도하면_403을_반환한다() throws Exception {
        doThrow(new ForbiddenAccessException())
                .when(chatQueryService)
                .checkChatUserValidation(anyLong(), anyLong());

        mockMvc.perform(get("/api/v1/chats/subscribe/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());

    }

    @Test
    void sendNewChatToCreateRoom_성공시_201을_반환한다() throws Exception {
        NewChatCommand command = new NewChatCommand(1L, "테스트");

        doNothing().when(chatCommandService).processStartNewChat(1L, command);


        mockMvc.perform(post("/api/v1/chats")
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void sendNewChatToCreateRoom_요청시_인증정보가_없으면_401을_반환한다() throws Exception {
        NewChatCommand command = new NewChatCommand(1L, "테스트");

        doNothing().when(chatCommandService).processStartNewChat(1L, command);


        mockMvc.perform(post("/api/v1/chats")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendNewChatToCreateRoom_RequestBody에_content값이_누락되면_400을_반환한다() throws Exception {
        NewChatCommand command = new NewChatCommand(1L, null);

        doNothing().when(chatCommandService).processStartNewChat(1L, command);


        mockMvc.perform(post("/api/v1/chats")
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("채팅 내용은 필수입니다."));
    }

    @Test
    void sendNewChatToCreateRoom_RequestBody에_receiverId값이_누락되면_400을_반환한다() throws Exception {
        NewChatCommand command = new NewChatCommand(null, "테스트");

        doNothing().when(chatCommandService).processStartNewChat(1L, command);


        mockMvc.perform(post("/api/v1/chats")
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수신자 ID는 필수입니다."));
    }


    @Test
    void sendNewChatToCreateRoom_RequestBody에_receiverId값이_양수가_아니면_400을_반환한다() throws Exception {
        NewChatCommand command = new NewChatCommand((long) -1, "테스트");

        doNothing().when(chatCommandService).processStartNewChat(1L, command);


        mockMvc.perform(post("/api/v1/chats")
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("수신자 ID는 양수입니다."));
    }

    @Test
    void sendNewChatToExistRoom_성공시_201을_반환한다() throws Exception {
        ExistChatCommand command = new ExistChatCommand("테스트");

        doNothing().when(chatCommandService).processResumeChat(1L, 1L, command);


        mockMvc.perform(post("/api/v1/chats/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    void sendNewChatToExistRoom_요청시_인증정보가_없으면_401을_반환한다() throws Exception {
        ExistChatCommand command = new ExistChatCommand("테스트");

        doNothing().when(chatCommandService).processResumeChat(1L, 1L, command);


        mockMvc.perform(post("/api/v1/chats/{chatRoomId}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void sendNewChatToExistRoom_RequestBody에_content값이_누락되면_400을_반환한다() throws Exception {
        ExistChatCommand command = new ExistChatCommand(null);

        mockMvc.perform(post("/api/v1/chats/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("채팅 내용은 필수입니다."));
    }

    @Test
    void getMessages를_cursor없이_요청성공시_200을_반환한다() throws Exception {

        when(chatQueryService.queryMessagesByChatRoomId(1L, null)).thenReturn(
                new ChatMessageQueryResult(
                        List.of(new ChatRecord(1L, "테스트", "12:00")),
                        "testcursor", true
                )
        );


        mockMvc.perform(get("/api/v1/chats/{chatRoomId}", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.nextCursor").exists())
                .andExpect(jsonPath("$.result.hasNext").exists());
    }

    @Test
    void getMessages를_cursor와_같이_요청성공시_200을_반환한다() throws Exception {
        when(chatQueryService.queryMessagesByChatRoomId(1L, "inputCursor")).thenReturn(
                new ChatMessageQueryResult(
                        List.of(new ChatRecord(1L, "테스트", "12:00")),
                        "testcursor", true
                )
        );


        mockMvc.perform(get("/api/v1/chats/{chatRoomId}?cursor=inputCursor", 1L)
                        .with(user(new AuthenticatedUserDetails(
                                1L,
                                "test@mail.com",
                                "테스터",
                                "pw"
                        )))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.nextCursor").exists())
                .andExpect(jsonPath("$.result.hasNext").exists());
    }

    @Test
    void getMessages를_인증정보_없이_요청시_401을_반환한다() throws Exception {
        when(chatQueryService.queryMessagesByChatRoomId(1L, "inputCursor")).thenReturn(
                new ChatMessageQueryResult(
                        List.of(new ChatRecord(1L, "테스트", "12:00")),
                        "testcursor", true
                )
        );


        mockMvc.perform(get("/api/v1/chats/{chatRoomId}?cursor=inputCursor", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
