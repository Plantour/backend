package com.qnelldo.plantour.mission.repository;

import com.qnelldo.plantour.mission.entity.MissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<MissionEntity, Long> {

    // 특정 반경 내의 미션을 찾는 메서드
    @Query("SELECT m FROM MissionEntity m WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(m.latitude)) * " +
            "cos(radians(m.longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(m.latitude)))) < :distance")
    List<MissionEntity> findMissionsWithinDistance(
            @Param("latitude") double latitude,
            @Param("longitude") double longitude,
            @Param("distance") double distanceInKm
    );

    // 특정 식물이 포함된 미션을 찾는 메서드
    @Query("SELECT m FROM MissionEntity m JOIN m.plants p WHERE p.id = :plantId")
    List<MissionEntity> findMissionsByPlantId(@Param("plantId") Long plantId);

    // 사용자의 현재 활성 미션을 찾는 메서드 (나중에 사용자 엔티티와 연결 시 구현)
    // List<Mission> findActiveToDoListForUser(Long userId);
}