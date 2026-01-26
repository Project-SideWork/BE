package com.sidework.profile.application.port.in;


public interface ProfileCommandUseCase
{
	void update(Long userId, ProfileUpdateCommand command);
}
