package com.sidework.project.application.adapter;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.PageResponse;
import com.sidework.project.application.port.in.ProjectApplyCommand;
import com.sidework.project.application.port.in.ProjectApplyDecisionCommand;
import com.sidework.project.application.port.in.ProjectApplyCommandUseCase;
import com.sidework.project.application.port.in.ProjectCommand;
import com.sidework.project.application.port.in.ProjectCommandUseCase;
import com.sidework.project.application.port.in.ProjectLikeCommandUseCase;
import com.sidework.project.application.port.in.ProjectQueryUseCase;

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
public class ProjectController {
    private final ProjectCommandUseCase commandService;
    private final ProjectApplyCommandUseCase applyCommandService;
    private final ProjectQueryUseCase queryService;
    private final ProjectLikeCommandUseCase likeCommandService;

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
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProject(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess(queryService.queryProjectDetail(projectId)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getProjectList(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.onSuccess(queryService.queryProjectList(user.getId(), pageable)));
    }

    @PostMapping("/{projectId}/like")
    public ResponseEntity<ApiResponse<Void>> likeProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @PathVariable("projectId") Long projectId) {
        likeCommandService.like(user.getId(), projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

}
