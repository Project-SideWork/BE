package com.sidework.common.response.exception;


import com.sidework.common.response.ApiResponse;
import com.sidework.common.response.ErrorDetail;
import com.sidework.common.response.status.ErrorStatus;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Objects;

@RestControllerAdvice(annotations = RestController.class)
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ExceptionAdvice extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> validation(
            ConstraintViolationException e,
            WebRequest request
    ) {

        String errorMessage = e.getConstraintViolations()
                .stream()
                .map((ConstraintViolation<?> violation) ->
                        String.format(
                                "prop '%s' | val '%s' | msg %s",
                                violation.getPropertyPath(),
                                violation.getInvalidValue(),
                                violation.getMessage()
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("ConstraintViolationException 추출 도중 에러 발생")
                );

        return handleExceptionInternalConstraint(
                e,
                HttpHeaders.EMPTY,
                request,
                errorMessage
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String message = ex.getMostSpecificCause() != null
                ? ex.getMostSpecificCause().getMessage()
                : "요청 본문을 읽을 수 없습니다.";

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorStatus.BAD_REQUEST.getCode(),
                ErrorStatus.BAD_REQUEST.getMessage(),
                message,
                path
        );

        return handleExceptionInternal(
                ex,
                body,
                headers,
                ErrorStatus.BAD_REQUEST.getHttpStatus(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> exception(
            Exception e,
            WebRequest request
    ) {
        StackTraceElement[] stackTrace = e.getStackTrace();
        String errorPoint = (stackTrace == null || stackTrace.length == 0)
                ? "No Stack Trace Error."
                : e.getStackTrace()[0].toString();

        return handleExceptionInternalFalse(
                e,
                ErrorStatus.INTERNAL_SERVER_ERROR,
                HttpHeaders.EMPTY,
                ErrorStatus.INTERNAL_SERVER_ERROR.getHttpStatus(),
                request,
                errorPoint
        );
    }

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<Object> handleGlobalException(
            GlobalException ex,
            HttpServletRequest request
    ) {
        ErrorDetail detail = ex.getErrorDetail();

        ApiResponse<Object> body = ApiResponse.onFailure(
                detail.code(),
                detail.message(),
                null,
                request.getRequestURI()
        );

        return new ResponseEntity<>(body, detail.httpStatus());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorStatus.METHOD_NOT_ALLOWED.getCode(),
                ErrorStatus.METHOD_NOT_ALLOWED.getMessage(),
                ex.getMessage() != null ? ex.getMessage() : "허용되지 않은 HTTP 메서드입니다.",
                path
        );

        return new ResponseEntity<>(body, ErrorStatus.METHOD_NOT_ALLOWED.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<String>> handleTypeMismatch(
            WebRequest request,
            MethodArgumentTypeMismatchException ex
    ) {
        String message = ex.getName() + "의 형식을 확인해주세요.";
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(
                        "COMMON_400",
                        "잘못된 요청입니다.",
                        message,
                        path
                )
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("잘못된 요청입니다.");

        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        return ResponseEntity.badRequest().body(
                ApiResponse.onFailure(
                        "COMMON_400",
                        errorMessage,
                        null,
                        path
                )
        );
    }

    /* =========================
       Internal Helper Methods
       ========================= */

    private ResponseEntity<Object> handleExceptionInternalFalse(
            Exception e,
            ErrorStatus errorStatus,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request,
            String errorPoint
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ApiResponse<Object> body = ApiResponse.onFailure(
                errorStatus.getCode(),
                errorStatus.getMessage(),
                errorPoint,
                path
        );

        log.error(errorPoint);
        return super.handleExceptionInternal(e, body, headers, status, request);
    }

    private ResponseEntity<Object> handleExceptionInternalConstraint(
            Exception e,
            HttpHeaders headers,
            WebRequest request,
            String message
    ) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();

        ApiResponse<Object> body = ApiResponse.onFailure(
                ErrorStatus.BAD_REQUEST.getCode(),
                message,
                null,
                path
        );

        log.error(message);
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                ErrorStatus.BAD_REQUEST.getHttpStatus(),
                request
        );
    }
}
