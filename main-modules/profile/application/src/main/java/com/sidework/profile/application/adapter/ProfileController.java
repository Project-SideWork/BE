package com.sidework.profile.application.adapter;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
import com.sidework.profile.application.port.in.ProfileLikeCommandUseCase;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController implements ProfileControllerDocs {
	private final ProfileCommandUseCase commandService;
	private final ProfileQueryUseCase profileQueryUseCase;
	private final ProfileLikeCommandUseCase profileLikeCommandUseCase;

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
		@AuthenticationPrincipal AuthenticatedUserDetails user) {
		return ResponseEntity.ok(ApiResponse.onSuccess(profileQueryUseCase.getProfileByUserId(user.getId())));
	}

	@PutMapping("/me")
	public ResponseEntity<ApiResponse<Void>> updateUserProfile(
		@AuthenticationPrincipal AuthenticatedUserDetails user,
		@RequestBody ProfileUpdateCommand profileUpdateCommand) {
		commandService.update(user.getId(), profileUpdateCommand);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
	}

	@GetMapping()
	public ResponseEntity<ApiResponse<PageResponse<List<UserProfileListResponse>>>> getUserProfiles(
		@AuthenticationPrincipal AuthenticatedUserDetails user,
		@PageableDefault(size = 20) Pageable pageable,
		@RequestParam(name = "skillIds", required = false) List<Long> skillIds){

		return ResponseEntity.ok(ApiResponse.onSuccess(profileQueryUseCase.getUserProfileList(user.getId(), skillIds, pageable)));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(@PathVariable("userId") Long userId) {
		return ResponseEntity.ok(ApiResponse.onSuccess(profileQueryUseCase.getProfileByUserId(userId)));
	}

	@PostMapping("/{profileId}/likes")
	public ResponseEntity<ApiResponse<Void>> likeUser(
		@AuthenticationPrincipal AuthenticatedUserDetails user,
		@PathVariable("profileId") Long profileId) {
		profileLikeCommandUseCase.like(user.getId(), profileId);
		return ResponseEntity.ok(ApiResponse.onSuccessVoid());
	}

	@GetMapping("/me/likes")
	public ResponseEntity<ApiResponse<PageResponse<List<UserProfileListResponse>>>> getLikedUserProfiles(
		@AuthenticationPrincipal AuthenticatedUserDetails user,
		@PageableDefault(size = 20) Pageable pageable,
		@RequestParam(name = "skillIds", required = false) List<Long> skillIds) {
		return ResponseEntity.ok(ApiResponse.onSuccess(profileQueryUseCase.getLikedUserProfileList(user.getId(), skillIds, pageable)));
	}


}
