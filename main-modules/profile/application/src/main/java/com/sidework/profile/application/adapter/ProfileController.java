package com.sidework.profile.application.adapter;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sidework.common.response.ApiResponse;
import com.sidework.profile.application.port.in.ProfileQueryUseCase;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {
	private final ProfileQueryUseCase queryService;

	//TODO: 로그인 연동
	@GetMapping
	public ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile()
	{
		return ResponseEntity.ok(ApiResponse.onSuccess(queryService.getProfileByUserId(1L)));
	}
}
