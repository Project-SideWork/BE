package com.sidework.skill.application.docs;

import com.sidework.common.response.ApiResponse;
import com.sidework.skill.application.adapter.SkillSearchResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "스킬 API")
public interface SkillControllerDocs {

	@Operation(description = "스킬 검색 (이름 키워드)")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
	})
	ResponseEntity<ApiResponse<List<SkillSearchResponse>>> search(
		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
	);
}
