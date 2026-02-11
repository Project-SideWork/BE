package com.sidework.notification.application.listener;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.sidework.notification.application.port.in.FcmPushUseCase;
import com.sidework.notification.application.port.in.NotificationCommandUseCase;
import com.sidework.notification.domain.Notification;
import com.sidework.notification.domain.NotificationType;
import com.sidework.project.application.event.ProjectApplyDecisionEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ProjectApplyDecisionListener {
	private final NotificationCommandUseCase notificationCommandUseCase;
	private final FcmPushUseCase fcmPushUseCase;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void on(ProjectApplyDecisionEvent event) {
		Long toUserId = event.applicantUserId();
		if (event.approved()) {
			notificationCommandUseCase.create(
				toUserId,
				NotificationType.PROJECT_APPROVED,
				"프로젝트 지원 결과 안내",
				String.format("'%s' 프로젝트에 %s 지원이 승인되었습니다.", event.projectTitle(),event.projectRole())
			);
			fcmPushUseCase.sendToUser(toUserId,"프로젝트 지원 결과 안내",
				String.format("'%s' 프로젝트에 %s 지원이 승인되었습니다.", event.projectTitle(),event.projectRole()));

		} else {
			notificationCommandUseCase.create(
				toUserId,
				NotificationType.PROJECT_REJECTED,
				"프로젝트 지원 결과 안내",
				String.format("'%s' 프로젝트에 %s 지원이 거절되었습니다.", event.projectTitle(),event.projectRole())
			);
				fcmPushUseCase.sendToUser(toUserId,"프로젝트 지원 결과 안내",
					String.format("'%s' 프로젝트에 %s 지원이 거절되었습니다.", event.projectTitle(),event.projectRole())
			);
		}
	}
}
