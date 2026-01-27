package com.sidework.profile.application.adapter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidework.common.response.ApiResponse;
import com.sidework.profile.application.port.in.ProfileCommandUseCase;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;

import lombok.RequiredArgsConstructor;

//TODO: 로그인 연동

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileQueryUseCase queryService;
	private final ProfileCommandUseCase commandService;

	@GetMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile()
	{
		return ResponseEntity.ok(ApiResponse.onSuccess(queryService.getProfileByUserId(1L)));
	}

	@PutMapping
	public ResponseEntity<ApiResponse<Void>> updateUserProfile(@RequestBody ProfileUpdateCommand profileUpdateCommand)
	{
		commandService.update(1L, profileUpdateCommand);
		return ResponseEntity.status(HttpStatus.OK).body(ApiResponse.onSuccessVoid());
	}
}
