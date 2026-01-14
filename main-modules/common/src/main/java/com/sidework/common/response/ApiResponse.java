package com.sidework.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sidework.common.response.status.SuccessStatus;


@JsonPropertyOrder({"code", "message", "result", "isSuccess"})
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean isSuccess, String code, String message, T result, String path) {

    // 성공 - 데이터 반환
    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(
                true,
                SuccessStatus.OK.getCode(),
                SuccessStatus.OK.getMessage(),
                result,
                null
        );
    }

    // 성공 - 생성됨 (201)
    public static <T> ApiResponse<T> onSuccessCreated() {
        return new ApiResponse<>(
                true,
                SuccessStatus.CREATED.getCode(),
                SuccessStatus.CREATED.getMessage(),
                null,
                null
        );
    }

    // 성공 - void 응답
    public static <T> ApiResponse<T> onSuccessVoid() {
        return new ApiResponse<>(
                true,
                SuccessStatus.OK.getCode(),
                SuccessStatus.OK.getMessage(),
                null,
                null
        );
    }

    // 실패 응답
    public static <T> ApiResponse<T> onFailure(
            String code,
            String message,
            T data,
            String requestUri
    ) {
        return new ApiResponse<>(
                false,
                code,
                message,
                data,
                requestUri
        );
    }
}