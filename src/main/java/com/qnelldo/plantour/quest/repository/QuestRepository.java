package com.qnelldo.plantour.quest.repository;

import com.qnelldo.plantour.common.enums.Season;
import com.qnelldo.plantour.quest.entity.QuestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestRepository extends JpaRepository<QuestEntity, Long> {
    Optional<QuestEntity> findBySeason(Season season);
}