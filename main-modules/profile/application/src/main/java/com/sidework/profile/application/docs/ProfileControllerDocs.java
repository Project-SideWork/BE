package com.sidework.profile.application.docs;

import com.sidework.common.auth.AuthenticatedUserDetails;
import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.PageResponse;
import com.sidework.profile.application.adapter.UserProfileListResponse;
import com.sidework.profile.application.adapter.UserProfileResponse;
import com.sidework.profile.application.adapter.UserProjectDto;
import com.sidework.profile.application.port.in.ProfileUpdateCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "프로필 API")
public interface ProfileControllerDocs {

    @Operation(description = "내 프로필 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
            @AuthenticationPrincipal AuthenticatedUserDetails user
    );

    @Operation(description = "내 프로젝트 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                        {
                                          "code": "COMMON_400",
                                          "message": "잘못된 요청입니다.",
                                          "isSuccess": false,
                                          "path": "/error"
                                        }
                                """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                        {
                                          "code": "COMMON_401",
                                          "message": "인증이 필요합니다.",
                                          "isSuccess": false,
                                          "path": "/error"
                                        }
                                """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<List<UserProjectDto>>>> getUserProfileProject(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "size", defaultValue = "5") Integer size
    );

    @Operation(description = "내 프로필 수정")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "400 예시",
                                    value = """
                                            {
                                              "code": "COMMON_400",
                                              "message": "잘못된 요청입니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> updateUserProfile(
            @AuthenticationPrincipal AuthenticatedUserDetails user,
            @RequestBody ProfileUpdateCommand profileUpdateCommand
    );

    @Operation(description = "프로필 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<List<UserProfileListResponse>>>> getUserProfiles(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds
    );

    @Operation(description = "사용자 프로필 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공")
    })
    ResponseEntity<ApiResponse<UserProfileResponse>> getUserProfile(
        @PathVariable("userId") Long userId
    );

    @Operation(description = "프로필 좋아요")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "처리 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<Void>> likeUser(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PathVariable("profileId") Long profileId
    );

    @Operation(description = "좋아요한 프로필 목록 조회")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "401 예시",
                                    value = """
                                            {
                                              "code": "COMMON_401",
                                              "message": "인증이 필요합니다.",
                                              "isSuccess": false,
                                              "path": "/error"
                                            }
                                    """
                            )
                    )
            )
    })
    ResponseEntity<ApiResponse<PageResponse<List<UserProfileListResponse>>>> getLikedUserProfiles(
        @AuthenticationPrincipal AuthenticatedUserDetails user,
        @PageableDefault(size = 20) Pageable pageable,
        @RequestParam(name = "skillIds", required = false) List<Long> skillIds
    );
}
