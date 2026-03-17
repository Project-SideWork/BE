package com.sidework.profile.application.adapter;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.PageResponse;
import com.sidework.profile.application.docs.ProfileControllerDocs;
import com.sidework.profile.application.port.in.ProfileCommandUseCase;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController implements ProfileControllerDocs {
	private final ProfileQueryUseCase queryService;
	private final ProfileCommandUseCase commandService;
	private final ProfileQueryUseCase profileQueryUseCase;

	@GetMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
		@AuthenticationPrincipal AuthenticatedUserDetails user) {
		return ResponseEntity.ok(ApiResponse.onSuccess(queryService.getProfileByUserId(user.getId())));
	}

	@PutMapping
	public ResponseEntity<ApiResponse<Void>> updateUserProfile(
		@AuthenticationPrincipal AuthenticatedUserDetails user,
		@RequestBody ProfileUpdateCommand profileUpdateCommand) {
		commandService.update(user.getId(), profileUpdateCommand);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
	}

	@GetMapping("/list")
	public ResponseEntity<ApiResponse<PageResponse<List<UserProfileListResponse>>>> getUserProfiles(
		@PageableDefault(size = 20) Pageable pageable,
		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword){

		return ResponseEntity.ok(ApiResponse.onSuccess(profileQueryUseCase.getUserProfileList(keyword,pageable)));

	}
}
