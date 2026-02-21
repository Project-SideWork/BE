package com.sidework.project.application.exception;

import com.sidework.common.response.exception.GlobalException;
import com.sidework.common.response.status.ErrorStatus;

public class ProjectHasNoMembersException extends GlobalException {

    public ProjectHasNoMembersException(Long projectId) {
        super(ErrorStatus.PROJECT_HAS_NO_MEMBERS.withDetail(
            String.format("해당 프로젝트(id=%d)에 멤버가 존재하지 않습니다.", projectId)
        ));
    }
}
