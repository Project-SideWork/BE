package com.sidework.profile.persistence.adapter;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.sidework.profile.application.port.out.RoleOutPort;
import com.sidework.profile.domain.Role;
import com.sidework.profile.persistence.entity.RoleEntity;
import com.sidework.profile.persistence.exception.RoleNotFoundException;
import com.sidework.profile.persistence.mapper.RoleMapper;
import com.sidework.profile.persistence.repository.RoleJpaRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolePersistenceAdapter implements RoleOutPort {

	private final RoleJpaRepository roleRepository;
	private final RoleMapper roleMapper;

	@Override
	public Role findById(Long id) {
		RoleEntity roleEntity = roleRepository.findById(id)
			.orElseThrow(RoleNotFoundException::new);
		return roleMapper.toDomain(roleEntity);
	}

	@Override
	public List<Role> findByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return List.of();
		}
		List<RoleEntity> roleEntities = roleRepository.findAllById(ids);
		return roleEntities.stream()
			.map(roleMapper::toDomain)
			.collect(Collectors.toList());
	}
}
