package com.qnelldo.plantour.mission.service;

import com.qnelldo.plantour.common.enums.Season;
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
        MissionEntity currentMission = missionRepository.findBySeason(currentSeason)
                .orElseThrow(() -> new RuntimeException("현재 계절의 미션을 찾을 수 없습니다."));

        List<MissionCompletionEntity> completions = missionCompletionRepository
                .findByUserAndMission(user, currentMission);

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
        String languageCode = userRepository.findLanguageCodeByEmail(userMission.getUser().getEmail());
        return new CompletedPuzzleInfo(
                userMission.getPuzzleNumber(),
                userMission.getPlant().getId(),
                userMission.getPlant().getName().get(languageCode),
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

        // 퍼즐 번호 유효성 검사
        if (puzzleNumber < 1 || puzzleNumber > 9) {
            throw new IllegalArgumentException("퍼즐 번호는 1부터 9 사이여야 합니다.");
        }

        // 해당 미션에 속한 식물인지 확인
        if (mission.getMissionPlants().stream().noneMatch(mp -> mp.getPlant().getId().equals(plantId))) {
            throw new RuntimeException("이 식물은 현재 미션에 속하지 않습니다.");
        }

        // 이미 이 식물로 퍼즐을 완성했는지 확인
        if (missionCompletionRepository.existsByUserAndMissionAndPlant(user, mission, plant)) {
            throw new RuntimeException("이 식물로 이미 퍼즐을 완성했습니다.");
        }

        // 이미 완성된 퍼즐 위치인지 확인
        if (missionCompletionRepository.existsByUserAndMissionAndPuzzleNumber(user, mission, puzzleNumber)) {
            throw new RuntimeException("이미 완성된 퍼즐 위치입니다.");
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

        return missionCompletionRepository.save(completion);
    }
}