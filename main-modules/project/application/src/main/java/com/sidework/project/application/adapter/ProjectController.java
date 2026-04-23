package com.sidework.project.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.PageResponse;
import com.sidework.project.application.dto.ProjectPromotionCommand;
import com.sidework.project.application.dto.ProjectUserReviewCommand;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.in.ProjectApplyCommandUseCase;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectCommandUseCase;
import com.sidework.project.application.port.in.ProjectLikeCommandUseCase;
import com.sidework.project.application.port.in.ProjectPromotionCommandUseCase;
import com.sidework.project.application.port.in.ProjectPromotionQueryUseCase;
import com.sidework.project.application.port.in.ProjectQueryUseCase;
import com.sidework.project.application.docs.ProjectControllerDocs;
import com.sidework.project.application.port.in.ProjectRetrospectiveCommand;
import com.sidework.project.application.port.in.ProjectRetrospectiveCommandUseCase;
import com.sidework.project.application.port.in.ProjectUserReviewCommandUseCase;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController implements ProjectControllerDocs {
    private final ProjectCommandUseCase commandService;
    private final ProjectApplyCommandUseCase applyCommandService;
    private final ProjectQueryUseCase queryService;
    private final ProjectLikeCommandUseCase likeCommandService;
    private final ProjectUserReviewCommandUseCase reviewService;
    private final ProjectPromotionCommandUseCase promotionCommandService;
    private final ProjectPromotionQueryUseCase promotionQueryService;
    private final ProjectRetrospectiveCommandUseCase retrospectiveCommandService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> postNewProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @Validated @RequestBody ProjectCommand command) {
        commandService.create(user.getId(), command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> patchProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectCommand command) {
        commandService.update(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId) {
        commandService.delete(user.getId(), projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @PostMapping("/{projectId}/apply")
    public ResponseEntity<ApiResponse<Void>> applyProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectApplyCommand command) {
        applyCommandService.apply(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @PatchMapping("/{projectId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectApplyDecisionCommand command) {
        applyCommandService.approve(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @PatchMapping("/{projectId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectApplyDecisionCommand command) {
        applyCommandService.reject(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProject(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess(queryService.queryProjectDetail(user.getId(), projectId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getProjectList(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(name = "skillIds", required = false) List<Long> skillIds) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                queryService.queryProjectList(user.getId(), keyword, skillIds, pageable)
            )
        );
    }

    @PostMapping("/{projectId}/likes")
    public ResponseEntity<ApiResponse<Void>> likeProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId) {
        likeCommandService.like(user.getId(), projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @DeleteMapping("/{projectId}/likes")
    public ResponseEntity<ApiResponse<Void>> deleteLikeProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId) {
        likeCommandService.delete(user.getId(), projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @GetMapping("/me/likes")
    public ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getLikedProjectList(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds){

        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                queryService.queryLikedProjectList(user.getId(), keyword, skillIds, pageable)
            )
        );
    }

    @PostMapping("/{projectId}/reviews")
    public ResponseEntity<ApiResponse<Void>> createMemberReview(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId,
        @Validated @RequestBody ProjectUserReviewCommand command) {
        reviewService.create(user.getId(), projectId, command);
        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
    }

    @PostMapping("/{projectId}/promotions")
    public ResponseEntity<ApiResponse<Void>> postNewProjectPromotion(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId,
        @Validated @RequestBody ProjectPromotionCommand command) {

        promotionCommandService.create(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @PatchMapping("/{projectId}/promotions/{promotionId}")
    public ResponseEntity<ApiResponse<Void>> patchProjectPromotion(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId,
        @PathVariable("promotionId") Long promotionId,
        @Validated @RequestBody ProjectPromotionCommand command) {

        promotionCommandService.update(user.getId(), promotionId, projectId, command);
        return ResponseEntity.ok(ApiResponse.onSuccessVoid());
    }

    @DeleteMapping("/{projectId}/promotions/{promotionId}")
    public ResponseEntity<ApiResponse<Void>> deleteProjectPromotion(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId,
        @PathVariable("promotionId") Long promotionId) {

        promotionCommandService.delete(user.getId(), promotionId, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @GetMapping("/promotions")
    public ResponseEntity<ApiResponse<PageResponse<List<ProjectPromotionListResponse>>>> getProjectPromotionList(
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds) {
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                promotionQueryService.queryProjectPromotionList(keyword, skillIds, pageable)
            )
        );
    }

    @GetMapping("/{projectId}/promotions/{promotionId}")
    public ResponseEntity<ApiResponse<ProjectPromotionDetailResponse>> getProjectPromotion(
        @PathVariable("projectId") Long projectId,
        @PathVariable("promotionId") Long promotionId){
        return ResponseEntity.ok(
            ApiResponse.onSuccess(
                promotionQueryService.queryProjectPromotionDetail(promotionId,projectId))
        );
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<MyProjectSummaryResponse>>> getMyProjects(
        @AuthenticationPrincipal AuthenticatedUserDetails user){
        return ResponseEntity.ok(ApiResponse.onSuccess(queryService.queryMyProjectSummary(user.getId())));
    }

    @PostMapping("/{projectId}/retrospectives")
    public ResponseEntity<ApiResponse<Void>> createProjectRetrospective(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("projectId") Long projectId,
        @Validated @RequestBody ProjectRetrospectiveCommand command
    ) {
        retrospectiveCommandService.create(user.getId(), projectId, command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }




}
