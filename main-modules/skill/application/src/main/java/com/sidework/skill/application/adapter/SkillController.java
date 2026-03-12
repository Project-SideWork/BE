package com.sidework.skill.application.adapter;

import com.sidework.common.response.ApiResponse;
import com.sidework.skill.application.docs.SkillControllerDocs;
import com.sidework.skill.application.port.in.SkillSearchQueryUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
public class SkillController implements SkillControllerDocs {

	private final SkillSearchQueryUseCase skillSearchQueryUseCase;

	@GetMapping("/search")
	public ResponseEntity<ApiResponse<List<SkillSearchResponse>>> search(
		@RequestParam(name = "keyword", required = false, defaultValue = "") String keyword
	) {
		return ResponseEntity.ok(ApiResponse.onSuccess(skillSearchQueryUseCase.searchByName(keyword)));
	}

}
