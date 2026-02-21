package com.sidework.project.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sidework.common.exception.InvalidCommandException;
import com.sidework.common.response.exception.ExceptionAdvice;
import com.sidework.project.application.adapter.ProjectController;
import com.sidework.project.application.exception.ProjectAlreadyAppliedException;
import com.sidework.project.application.exception.ProjectDeleteAuthorityException;
import com.sidework.project.application.exception.ProjectNotRecruitingException;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.exception.ProjectNotFoundException;
import com.sidework.project.application.exception.ProjectHasNoMembersException;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.in.ProjectApplyCommandUseCase;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectCommandUseCase;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.port.in.RecruitPosition;
import com.sidework.project.domain.SkillLevel;
import com.sidework.project.application.port.out.ProjectOutPort;
import com.sidework.project.domain.MeetingType;
import com.sidework.project.domain.ApplyStatus;
import com.sidework.project.domain.Project;
import com.sidework.project.domain.ProjectRole;
import com.sidework.project.domain.ProjectStatus;
import com.sidework.skill.application.port.out.SkillOutPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = ProjectTestApplication.class)
@Import(ExceptionAdvice.class)
public class ProjectControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectCommandUseCase projectCommandUseCase;

    @MockitoBean
    private ProjectApplyCommandUseCase projectApplyCommandUseCase;

    @MockitoBean
    private ProjectQueryUseCase projectQueryUseCase;

    @MockitoBean
    private ProjectOutPort repo;

    @MockitoBean
    private SkillOutPort skillRepo;

    @Test
    void 프로젝트_게시글_생성_요청시_성공하면_201을_반환한다() throws Exception {
        ProjectCommand command = createCommand(ProjectStatus.PREPARING);
        doNothing().when(projectCommandUseCase).create(command);

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
        .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void 프로젝트_게시글_생성_요청시_어느_하나의_값이라도_Null이면_400을_반환한다() throws Exception {
        ProjectCommand command = createNullCommand();

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }

    @Test
    void 프로젝트_게시글_생성_요청시_필수_기술_스택이_빈_배열이면_400을_반환한다() throws Exception {
        ProjectCommand command = createRequiredSkillEmptyCommand();

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_생성_요청시_필수_기술_스택에_카테고리의_ID가_포함되면_400을_반환한다() throws Exception {
        ProjectCommand command = createCommand(ProjectStatus.RECRUITING);
        doThrow(new InvalidCommandException("존재하지 않거나 비활성화된 기술 id: " + 1L))
                .when(projectCommandUseCase)
                        .create(command);

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_생성_요청시_우대_기술_스택에_카테고리의_ID가_포함되면_400을_반환한다() throws Exception {
        ProjectCommand command = createCommand(ProjectStatus.RECRUITING);
        doThrow(new InvalidCommandException("존재하지 않거나 비활성화된 기술 id: " + 2L))
                .when(projectCommandUseCase)
                .create(command);

        mockMvc.perform(post("/api/v1/projects")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void 프로젝트_게시글_생성_요청시_지원하지_않는_ENUM이_포함되면_400을_반환한다() throws Exception {
        String invalidJson = """
        {
          "title": "버스 실시간 위치 서비스",
          "description": "WebSocket 기반 실시간 위치 공유 프로젝트",
          "role": "BACKEND",
          "recruitPositions": [
            {
              "role": "BACKEND",
              "count": 1,
              "skillLevel": "JUNIOR"
            }
          ],
          "startDt": "2025-01-01",
          "endDt": "2025-03-31",
          "meetingType": "OFLINE",
          "meetingDetail": "주 2회 온라인",
          "requiredStacks": ["Spring Boot"],
          "preferredStacks": ["Redis"],
          "status": "RECRUITING"
        }
        """;

        mockMvc.perform(post("/api/v1/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_성공하면_200을_반환한다() throws Exception {
        ProjectCommand command = createUpdateCommand();
        Long projectId = 1L;

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_어느_하나의_값이라도_Null이면_400을_반환한다() throws Exception {
        ProjectCommand command = createNullCommand();
        Long projectId = 1L;

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_어느_모집인원이_0이하이면_400을_반환한다() throws Exception {
        ProjectCommand command = createInvalidCountCommand();
        Long projectId = 1L;

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_필수_기술_스택에_카테고리의_ID가_포함되면_400을_반환한다() throws Exception {
        ProjectCommand command = createCommand(ProjectStatus.RECRUITING);
        Long projectId = 1L;
        doThrow(new InvalidCommandException("존재하지 않거나 비활성화된 기술 id: " + 1L))
                .when(projectCommandUseCase)
                .update(projectId, command);

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_우대_기술_스택에_카테고리의_ID가_포함되면_400을_반환한다() throws Exception {
        ProjectCommand command = createCommand(ProjectStatus.RECRUITING);
        Long projectId = 1L;
        doThrow(new InvalidCommandException("존재하지 않거나 비활성화된 기술 id: " + 3L))
                .when(projectCommandUseCase)
                .update(projectId, command);

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_필수_기술_스택이_빈_배열이면_400을_반환한다() throws Exception {
        ProjectCommand command = createRequiredSkillEmptyCommand();
        Long projectId = 1L;

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_게시글_수정_요청시_지원하지_않는_ENUM이_포함되면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        String invalidJson = """
        {
          "title": "버스 실시간 위치 서비스",
          "description": "WebSocket 기반 실시간 위치 공유 프로젝트",
          "role": "BACKEND",
          "recruitPositions": [
            {
              "role": "BACKEND",
              "count": 1,
              "skillLevel": "JUNIOR"
            }
          ],
          "startDt": "2025-01-01",
          "endDt": "2025-03-31",
          "meetingType": "OFLINE",
          "meetingDetail": "주 2회 온라인",
          "requiredStacks": ["Spring Boot"],
          "preferredStacks": ["Redis"],
          "status": "RECRUITING"
        }
        """;

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_수정_요청시_projectId가_존재하지_않는_값이라면_404를_반환한다() throws Exception {
        ProjectCommand command = createUpdateCommand();
        Long projectId = 999L;

        doThrow(new ProjectNotFoundException(projectId))
                .when(projectCommandUseCase)
                .update(projectId, command);

        mockMvc.perform(patch("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 프로젝트_지원_요청시_성공하면_200을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

        doNothing().when(projectApplyCommandUseCase).apply(1L, projectId, command);

        mockMvc.perform(post("/api/v1/projects/{projectId}/apply", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void 프로젝트_지원_요청시_profileId나_role이_null이면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        String invalidJson = """
        {
          "profileId": null,
          "role": null
        }
        """;

        mockMvc.perform(post("/api/v1/projects/{projectId}/apply", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_지원_요청시_프로젝트가_없으면_404를_반환한다() throws Exception {
        Long projectId = 999L;
        ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

        doThrow(new ProjectNotFoundException(projectId))
                .when(projectApplyCommandUseCase).apply(eq(2L), eq(projectId), any(ProjectApplyCommand.class));

        mockMvc.perform(post("/api/v1/projects/{projectId}/apply", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 프로젝트_지원_요청시_모집중이_아니면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

        doThrow(new ProjectNotRecruitingException(projectId))
                .when(projectApplyCommandUseCase).apply(eq(2L), eq(projectId), any(ProjectApplyCommand.class));

        mockMvc.perform(post("/api/v1/projects/{projectId}/apply", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_지원_요청시_이미_지원한_프로젝트이면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectApplyCommand command = new ProjectApplyCommand(1L, ProjectRole.BACKEND);

        doThrow(new ProjectAlreadyAppliedException(projectId))
                .when(projectApplyCommandUseCase).apply(eq(2L), eq(projectId), any(ProjectApplyCommand.class));

        mockMvc.perform(post("/api/v1/projects/{projectId}/apply", projectId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_승인_요청시_바디로_전달하면_200을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectApplyDecisionCommand command = new ProjectApplyDecisionCommand(2L, ProjectRole.BACKEND);

        doNothing().when(projectApplyCommandUseCase).approve(anyLong(), eq(projectId), any(ProjectApplyDecisionCommand.class));

        mockMvc.perform(patch("/api/v1/projects/{projectId}/approve", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void 프로젝트_승인_요청시_applicantUserId나_role이_null이면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        String invalidJson = """
        {
          "applicantUserId": null,
          "role": null
        }
        """;

        mockMvc.perform(patch("/api/v1/projects/{projectId}/approve", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 프로젝트_거절_요청시_바디로_전달하면_200을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectApplyDecisionCommand command = new ProjectApplyDecisionCommand(3L, ProjectRole.FRONTEND);

        doNothing().when(projectApplyCommandUseCase).reject(anyLong(), eq(projectId), any(ProjectApplyDecisionCommand.class));

        mockMvc.perform(patch("/api/v1/projects/{projectId}/reject", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true));
    }

    @Test
    void 프로젝트_거절_요청시_applicantUserId나_role이_null이면_400을_반환한다() throws Exception {
        Long projectId = 1L;
        String invalidJson = """
        {
          "applicantUserId": null,
          "role": null
        }
        """;

        mockMvc.perform(patch("/api/v1/projects/{projectId}/reject", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void 존재하는_프로젝트_삭제_요청시_사용자가_삭제_권한이_있다면_200을_반환한다() throws Exception {
        Long projectId = 1L;

        mockMvc.perform(delete("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    void 존재하는_프로젝트_삭제_요청시_사용자가_삭제_권한이_없다면_403을_반환한다() throws Exception {
        Long projectId = 1L;
        doThrow(new ProjectDeleteAuthorityException(projectId))
                .when(projectCommandUseCase)
                .delete(anyLong(), eq(projectId));

        mockMvc.perform(delete("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void 프로젝트_삭제_요청시_projectId가_존재하지_않는_값이라면_404를_반환한다() throws Exception {
        ProjectCommand command = createUpdateCommand();
        Long userId = 1L;
        Long projectId = 999L;

        doThrow(new ProjectNotFoundException(projectId))
                .when(projectCommandUseCase)
                .delete(userId, projectId);

        mockMvc.perform(delete("/api/v1/projects/{projectId}", projectId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 프로젝트_상세_조회_요청시_성공하면_200과_상세_응답을_반환한다() throws Exception {
        Long projectId = 1L;
        ProjectDetailResponse detail = new ProjectDetailResponse(
            1L,
            "테스트 프로젝트",
            "설명",
            java.time.LocalDate.of(2025, 1, 1),
            java.time.LocalDate.of(2025, 3, 31),
            MeetingType.HYBRID,
            ProjectStatus.RECRUITING,
            List.of(ProjectDetailResponse.ProjectMemberResponse.of(1L, 10L, ProjectRole.OWNER, ApplyStatus.ACCEPTED)),
            List.of(ProjectDetailResponse.RecruitPositionResponse.of(ProjectRole.BACKEND, 1, 0, SkillLevel.JUNIOR)),
            List.of("Java", "Spring"),
            List.of("Redis")
        );
        when(projectQueryUseCase.queryProjectDetail(projectId)).thenReturn(detail);

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.result.id").value(1))
                .andExpect(jsonPath("$.result.title").value("테스트 프로젝트"))
                .andExpect(jsonPath("$.result.teamMembers").isArray())
                .andExpect(jsonPath("$.result.requiredStacks").isArray())
                .andExpect(jsonPath("$.result.preferredStacks").isArray());
    }

    @Test
    void 프로젝트_상세_조회_요청시_projectId가_존재하지_않으면_404를_반환한다() throws Exception {
        Long projectId = 999L;
        doThrow(new ProjectNotFoundException(projectId))
                .when(projectQueryUseCase).queryProjectDetail(projectId);

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void 프로젝트_상세_조회_요청시_멤버가_없으면_500을_반환한다() throws Exception {
        Long projectId = 1L;
        doThrow(new ProjectHasNoMembersException(projectId))
                .when(projectQueryUseCase).queryProjectDetail(projectId);

        mockMvc.perform(get("/api/v1/projects/{projectId}", projectId))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }

    private ProjectCommand createCommand(ProjectStatus status) {
        return new ProjectCommand(
                "버스 실시간 위치 서비스",                 // title
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole  .BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 3, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(1L, 2L), // requiredStacks
                List.of(3L, 4L),       // preferredStacks
                status         // status
        );
    }

    private ProjectCommand createRequiredSkillEmptyCommand() {
        return new ProjectCommand(
                "버스 실시간 위치 서비스",                 // title
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole  .BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 3, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(), // requiredStacks
                List.of(3L, 4L),       // preferredStacks
                ProjectStatus.RECRUITING         // status
        );
    }

    private ProjectCommand createNullCommand() {
        return new ProjectCommand(
                null,
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                2,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 12, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(1L, 2L), // requiredStacks
                List.of(3L, 4L),       // preferredStacks
                ProjectStatus.RECRUITING          // status
        );
    }

    private ProjectCommand createInvalidCountCommand() {
        return new ProjectCommand(
                null,
                "WebSocket 기반 실시간 위치 공유 프로젝트", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                1,
                                SkillLevel.JUNIOR
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                0,
                                SkillLevel.MID
                        )
                ),
                LocalDate.of(2025, 1, 1),   // startDt
                LocalDate.of(2025, 12, 31),  // endDt
                MeetingType.HYBRID,         // meetingType
                "주 2회 온라인, 월 1회 오프라인", // meetingDetail
                List.of(1L, 2L), // requiredStacks
                List.of(3L, 4L),       // preferredStacks
                ProjectStatus.RECRUITING          // status
        );
    }


    private ProjectCommand createUpdateCommand() {
        return new ProjectCommand(
                "AI 기반 관광 코스 추천 서비스",              // title
                "사용자 위치와 혼잡도를 반영한 여행 코스 추천", // description
                ProjectRole.BACKEND,
                List.of(
                        new RecruitPosition(
                                ProjectRole.BACKEND,
                                2,
                                SkillLevel.MID
                        ),
                        new RecruitPosition(
                                ProjectRole.FRONTEND,
                                1,
                                SkillLevel.JUNIOR
                        )
                ),
                LocalDate.of(2025, 4, 1),   // startDt
                LocalDate.of(2025, 7, 31),  // endDt
                MeetingType.ONLINE,         // meetingType
                "전면 온라인, 필요 시 비동기 협업", // meetingDetail
                List.of(1L, 2L), // requiredStacks
                List.of(3L, 4L),       // preferredStacks
                ProjectStatus.PREPARING                         // status
        );
    }
    private Project createProject(
            ProjectCommand command
    ) {
        return new Project(
                null,
                command.title(),
                command.description(),
                command.startDt(),
                command.endDt(),
                command.meetingType(),
                command.status()
        );
    }
}
