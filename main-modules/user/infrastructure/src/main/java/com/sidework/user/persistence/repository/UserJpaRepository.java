package com.sidework.user.persistence.repository;

import com.sidework.user.application.port.out.GithubInfoDto;
import com.sidework.user.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByEmail(String email);
    boolean existsByNickname(String nickname);
    boolean existsByTel(String tel);

    @Query("SELECT COUNT(e) > 0 FROM UserEntity e WHERE e.email = :email AND e.id <> :excludeUserId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("excludeUserId") Long excludeUserId);

    @Query("SELECT COUNT(e) > 0 FROM UserEntity e WHERE e.nickname = :nickname AND e.id <> :excludeUserId")
    boolean existsByNicknameAndIdNot(@Param("nickname") String nickname, @Param("excludeUserId") Long excludeUserId);

    @Query("SELECT COUNT(e) > 0 FROM UserEntity e WHERE e.tel = :tel AND e.id <> :excludeUserId")
    boolean existsByTelAndIdNot(@Param("tel") String tel, @Param("excludeUserId") Long excludeUserId);

    UserEntity findByEmail(String email);

    @Query("SELECT new com.sidework.user.application.port.out.GithubInfoDto(e.githubId, e.githubAccessToken) FROM UserEntity e WHERE e.id = :id")
    GithubInfoDto findGithubInfoById(@Param("id") Long id);
}
