package com.sidework.profile.persistence.repository;

import java.util.List;

import com.sidework.profile.persistence.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleEntity, Long> {
	RoleEntity findById(long id);
	List<RoleEntity> findByIds(List<Long> ids);

}

