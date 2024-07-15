package com.qnelldo.plantour.mission.service;

import com.qnelldo.plantour.enums.Season;
import com.qnelldo.plantour.mission.entity.MissionEntity;
import com.qnelldo.plantour.mission.repository.MissionRepository;
import com.qnelldo.plantour.mission.dto.CompletedPuzzleInfo;
import com.qnelldo.plantour.mission.dto.UserMissionProgressDto;
import com.qnelldo.plantour.mission.entity.MissionCompletionEntity;
import com.qnelldo.plantour.plant.entity.PlantEntity;
import com.qnelldo.plantour.plant.repository.PlantRepository;
import com.qnelldo.plantour.user.entity.UserEntity;
import com.qnelldo.plantour.mission.repository.MissionCompletionRepository;
import com.qnelldo.plantour.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MissionCompletionService {

    private final UserRepository userRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final MissionService missionService;
    private final MissionRepository missionRepository;
    private final PlantRepository plantRepository;

    @Autowired
    public MissionCompletionService(UserRepository userRepository,
                                    MissionCompletionRepository missionCompletionRepository,
                                    MissionService missionService,
                                    MissionRepository missionRepository,
                                    PlantRepository plantRepository) {
        this.userRepository = userRepository;
        this.missionCompletionRepository = missionCompletionRepository;
        this.missionService = missionService;
        this.missionRepository = missionRepository;
        this.plantRepository = plantRepository;
    }

    public UserMissionProgressDto getUserMissionProgress(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Season currentSeason = missionService.getCurrentSeason();

        List<MissionCompletionEntity> completions = missionCompletionRepository
                .findByUserIdAndMissionSeason(userId, currentSeason);

        List<CompletedPuzzleInfo> completedPuzzles = completions.stream()
                .map(this::mapToCompletedPuzzleInfo)
                .collect(Collectors.toList());

        return new UserMissionProgressDto(
                user.getId(),
                user.getName(),
                currentSeason,
                completedPuzzles
        );
    }

    private CompletedPuzzleInfo mapToCompletedPuzzleInfo(MissionCompletionEntity userMission) {
        return new CompletedPuzzleInfo(
                userMission.getPuzzleNumber(),
                userMission.getPlant().getId(),
                userMission.getPlant().getName(),
                userMission.getCompletedAt()
        );
    }

    /**
     * 미션 퍼즐 조각을 완성하고 기록을 저장합니다.
     * 이미 완성된 퍼즐 조각에 대해서는 예외를 발생시킵니다.
     */
    public MissionCompletionEntity completeMissionPuzzle(Long userId, Long missionId, int puzzleNumber, Long plantId,
                                                         String content, String image, Double latitude, Double longitude) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        MissionEntity mission = missionRepository.findById(missionId)
                .orElseThrow(() -> new RuntimeException("미션을 찾을 수 없습니다."));

        PlantEntity plant = plantRepository.findById(plantId)
                .orElseThrow(() -> new RuntimeException("식물을 찾을 수 없습니다."));

        // 이미 완료된 퍼즐인지 확인
        if (missionCompletionRepository.existsByUserAndMissionAndPuzzleNumber(user, mission, puzzleNumber)) {
            throw new RuntimeException("이미 완료된 퍼즐입니다.");
        }

        MissionCompletionEntity completion = new MissionCompletionEntity();
        completion.setUser(user);
        completion.setMission(mission);
        completion.setPlant(plant);
        completion.setPuzzleNumber(puzzleNumber);
        completion.setContent(content);
        completion.setImage(image);
        completion.setLatitude(latitude);
        completion.setLongitude(longitude);
        completion.setCompletedAt(LocalDateTime.now());

        // 미션의 완성된 퍼즐 수 증가
        mission.completePuzzle();
        missionRepository.save(mission);

        return missionCompletionRepository.save(completion);
    }
}