package com.sidework.profile.application.port.out;

import java.util.List;

import com.sidework.profile.domain.Role;

public interface RoleOutPort
{
	Role findById(Long id);
	List<Role> findByIds(List<Long> ids);
}
