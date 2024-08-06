package com.qnelldo.plantour.user.repository;

import com.qnelldo.plantour.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);

    String findLanguageCodeByEmail(String email);
    Optional<UserEntity> findByProviderId(String providerId);
}