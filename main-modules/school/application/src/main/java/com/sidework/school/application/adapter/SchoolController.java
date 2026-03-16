package com.sidework.school.application.adapter;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sidework.common.response.ApiResponse;
import com.sidework.school.application.port.in.SchoolQueryUseCase;
import com.sidework.school.application.service.SchoolQueryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schools")
@RequiredArgsConstructor
public class SchoolController {

	private final SchoolQueryUseCase schoolQueryUseCase;

	@GetMapping
	public ResponseEntity<ApiResponse<List<SchoolResponse>>> getAllSchools(
	) {
		return ResponseEntity.ok(ApiResponse.onSuccess(schoolQueryUseCase.findAll()));
	}

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<SchoolResponse>>> search(
		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
	) {
		return ResponseEntity.ok(ApiResponse.onSuccess(schoolQueryUseCase.searchByName(keyword)));
	}
}
