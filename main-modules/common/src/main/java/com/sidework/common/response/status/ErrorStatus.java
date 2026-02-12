package com.sidework.common.response.status;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorStatus implements BaseStatusCode {

    // 일반 응답
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "금지된 요청입니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "허용되지 않은 요청 메서드입니다."),

    // ENUM
    INVALID_ENUM(HttpStatus.BAD_REQUEST, "ENUM_001", "지원하지 않는 Enum 값입니다."),

    // JWT
    EMPTY_JWT(HttpStatus.UNAUTHORIZED, "TOKEN_001", "토큰이 비어있습니다."),
    INVALID_JWT(HttpStatus.UNAUTHORIZED, "TOKEN_002", "필요한 정보를 포함하지 않은 토큰입니다."),
    EXPIRED_JWT(HttpStatus.UNAUTHORIZED, "TOKEN_003", "유효기간이 만료된 토큰입니다."),

    // USER
    USER_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_001", "이미 존재하는 사용자입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "해당 사용자를 찾을 수 없습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER_003", "이미 사용 중인 이메일입니다."),

    // PROJECT
    PROJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROJECT_001", "해당 프로젝트를 찾을 수 없습니다."),
    PROJECT_CANNOT_UPDATE(HttpStatus.BAD_REQUEST, "PROJECT_002", "해당 프로젝트는 수정할 수 없습니다."),
    PROJECT_CANNOT_DELETE(HttpStatus.FORBIDDEN, "PROJECT_003", "해당 프로젝트를 삭제할 권한이 없습니다."),
    PROJECT_NOT_RECRUITING(HttpStatus.BAD_REQUEST, "PROJECT_004", "현재 모집 중인 프로젝트가 아닙니다."),
    PROJECT_ALREADY_APPLIED(HttpStatus.BAD_REQUEST, "PROJECT_005", "이미 지원한 프로젝트입니다."),
    PROJECT_OWNER_NOT_FOUND(HttpStatus.FORBIDDEN, "PROJECT_006", "해당 프로젝트의 소유자가 아닙니다."),
    PROJECT_APPLICANT_NOT_FOUND(HttpStatus.FORBIDDEN, "PROJECT_007", "해당 프로젝트의 신청자가 아닙니다."),
    PROJECT_USER_NOT_FOUND(HttpStatus.FORBIDDEN, "PROJECT_008", "해당 프로젝트의 유저/소유자가 아닙니다."),
    PROJECT_APPLY_ALREADY_PROCESSED(HttpStatus.CONFLICT, "PROJECT_009", "이미 처리된 지원입니다."),

    // PROFILE
    PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE_001", "해당 프로필을 찾을 수 없습니다."),
    PROFILE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PROFILE_002", "이미 프로필이 존재합니다."),

    // ROLE
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROLE_001", "해당 역할을 찾을 수 없습니다."),

    // SCHOOL
    SCHOOL_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHOOL_001", "해당 학교를 찾을 수 없습니다."),

    // SKILL
    SKILL_NOT_FOUND(HttpStatus.NOT_FOUND, "SKILL_001", "해당 스킬을 찾을 수 없습니다."),

    // PORTFOLIO
    PORTFOLIO_NOT_FOUND(HttpStatus.NOT_FOUND, "PORTFOLIO_001", "해당 대외활동을 찾을 수 없습니다."),

    //NOTIFICATION
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION_001", "해당 알림을 찾을 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    ErrorStatus(HttpStatus httpStatus, String code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }


    @Override
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public BaseStatusCode withDetail(String detail) {
        return new BaseStatusCode() {
            @Override
            public HttpStatus getHttpStatus() {
                return ErrorStatus.this.httpStatus;
            }

            @Override
            public String getCode() {
                return ErrorStatus.this.code;
            }

            @Override
            public String getMessage() {
                return detail;
            }
        };
    }
}
