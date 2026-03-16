package com.sidework.school.application.docs;

import com.sidework.common.response.ApiResponse;
import com.sidework.school.application.adapter.SchoolResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "학교 API")
public interface SchoolControllerDocs {

	@Operation(description = "학교 전체 목록 조회")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
	})
	ResponseEntity<ApiResponse<List<SchoolResponse>>> getAllSchools();

	@Operation(description = "학교 이름 검색")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
	})
	ResponseEntity<ApiResponse<List<SchoolResponse>>> search(
		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
	);
}
