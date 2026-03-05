package com.sidework.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "FCM 테스트 페이지")
public interface FcmTestControllerDocs {

    @Operation(description = "FCM 테스트 HTML 페이지로 포워딩")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "포워딩 성공")
    })
    String fcmTest();
}
