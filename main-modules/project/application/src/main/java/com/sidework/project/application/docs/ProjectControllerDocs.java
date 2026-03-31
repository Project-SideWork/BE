package com.sidework.project.application.docs;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.PageResponse;
import com.sidework.project.application.adapter.MyProjectSummaryResponse;
import com.sidework.project.application.adapter.ProjectDetailResponse;
import com.sidework.project.application.adapter.ProjectListResponse;
import com.sidework.project.application.adapter.ProjectPromotionListResponse;
import com.sidework.project.application.dto.ProjectPromotionCommand;
import com.sidework.project.application.dto.ProjectUserReviewCommand;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.in.ProjectCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "프로젝트 API")
public interface ProjectControllerDocs {

    @Operation(description = "프로젝트 생성")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> postNewProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestBody ProjectCommand command
    );

    @Operation(description = "프로젝트 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "COMMON_404",
                                              "message": "프로젝트를 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> patchProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectCommand command
    );

    @Operation(description = "프로젝트 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "403 예시",
                                    value = """
                                            {
                                              "code": "COMMON_403",
                                              "message": "권한이 부족합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "COMMON_404",
                                              "message": "프로젝트를 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId
    );

    @Operation(description = "프로젝트 지원")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "지원 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    ))
    })
    ResponseEntity<ApiResponse<Void>> applyProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectApplyCommand command
    );

    @Operation(description = "프로젝트 지원 승인")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "승인 성공")
    })
    ResponseEntity<ApiResponse<Void>> approveProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectApplyDecisionCommand command
    );

    @Operation(description = "프로젝트 지원 거절")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거절 성공")
    })
    ResponseEntity<ApiResponse<Void>> rejectProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @RequestBody ProjectApplyDecisionCommand command
    );

    @Operation(description = "프로젝트 상세 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "프로젝트 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "COMMON_404",
                                              "message": "프로젝트를 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<ProjectDetailResponse>> getProject(
            @PathVariable("projectId") Long projectId
    );

    @Operation(description = "프로젝트 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })

    ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getProjectList(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds
    );

    @Operation(description = "프로젝트 좋아요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공")
    })
    ResponseEntity<ApiResponse<Void>> likeProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId
    );

    @Operation(description = "좋아요한 프로젝트 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getLikedProjectList(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds
    );

    @Operation(
        description = "프로젝트 동료 평가"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청 (검증 실패, 프로젝트 미종료, 미승인 멤버, 자기 자신 평가 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 해당 팀원에 대해 평가를 등록한 경우",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "409 예시",
                                    value = """
                                            {
                                              "code": "PROJECT_014",
                                              "message": "이미 평가한 팀원입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> createMemberReview(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectUserReviewCommand command
    );

    @Operation(description = "프로젝트 홍보글 등록")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패, 프로젝트 미종료, 최근 홍보 이력 등",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> postNewProjectPromotion(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectPromotionCommand command
    );

    @Operation(description = "프로젝트 홍보글 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "검증 실패, 경로의 프로젝트와 홍보 대상 프로젝트 불일치 등",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "홍보글 없음 (본인 글 아님 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "PROJECT_PROMOTION_001",
                                              "message": "해당 프로젝트 홍보글을 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> patchProjectPromotion(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @PathVariable("promotionId") Long promotionId,
            @Validated @RequestBody ProjectPromotionCommand command
    );

    @Operation(description = "프로젝트 홍보글 삭제")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "경로의 프로젝트와 홍보 대상 프로젝트 불일치",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "홍보글 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "404 예시",
                                    value = """
                                            {
                                              "code": "PROJECT_PROMOTION_001",
                                              "message": "해당 프로젝트 홍보글을 찾을 수 없습니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> deleteProjectPromotion(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @PathVariable("promotionId") Long promotionId
    );

    @Operation(description = "프로젝트 홍보글 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<PageResponse<List<ProjectPromotionListResponse>>>> getProjectPromotionList(
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds
    );

    @Operation(description = "내 참여 프로젝트 요약 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<List<MyProjectSummaryResponse>>> getMyProjects(
            @AuthenticationPrincipal AuthenticatedUserDetails user
    );

}
