package com.sidework.project.application.adapter;

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
    public ResponseEntity<ApiResponse<Void>> postNewProject(@Validated @RequestBody ProjectCommand command) {
        commandService.create(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.onSuccessCreated());
    }

    @PatchMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> patchProject(@PathVariable("projectId") Long projectId, @Validated @RequestBody ProjectCommand command) {
        commandService.update(projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    // TODO: UserDetail 변경
    @DeleteMapping("/{projectId}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable("projectId") Long projectId) {
        commandService.delete(1L, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    // TODO: UserDetail 변경
    @PostMapping("/{projectId}/apply")
    public ResponseEntity<ApiResponse<Void>> applyProject(@PathVariable("projectId") Long projectId, @Validated @RequestBody ProjectApplyCommand command) {
        applyCommandService.apply(2L,projectId,command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @PatchMapping("/{projectId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveProject(
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectApplyDecisionCommand command) {
        applyCommandService.approve(2L, projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @PatchMapping("/{projectId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectProject(
            @PathVariable("projectId") Long projectId,
            @Validated @RequestBody ProjectApplyDecisionCommand command) {
        applyCommandService.reject(2L, projectId, command);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

    @GetMapping("/{projectId}")
    public ResponseEntity<ApiResponse<ProjectDetailResponse>> getProject(@PathVariable("projectId") Long projectId) {
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccess(queryService.queryProjectDetail(projectId)));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<PageResponse<List<ProjectListResponse>>>> getProjectList(
        @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.onSuccess(queryService.queryProjectList(1L,pageable)));
    }

    @PostMapping("/{projectId}/like")
    public ResponseEntity<ApiResponse<Void>> likeProject(@PathVariable("projectId") Long projectId) {
        likeCommandService.like(1L, projectId);
        return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
    }

}
