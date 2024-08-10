package com.qnelldo.plantour.user.repository;

import com.qnelldo.plantour.user.entity.Nickname;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface NicknameRepository extends JpaRepository<Nickname, Long> {
    @Query(value = "SELECT * FROM nicknames ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Optional<Nickname> findRandomNickname();
}
