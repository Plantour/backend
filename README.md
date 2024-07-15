# Plantour 프로젝트 백엔드 진행 상황 업데이트
## 완료된 작업

### 사용자 관리
- [x] UserEntity 설계
- [x] UserRepository 구현
- [x] UserService 구현
- [x] UserController 구현
- [ ] ~~Security Config 설정~~ (현재 주석 처리됨)

### 미션 시스템
- [x] MissionEntity 설계
- [x] MissionRepository 구현
- [x] MissionService 구현
- [x] MissionController 구현
- [x] MissionCompletionEntity 설계
- [x] MissionCompletionRepository 구현
- [x] MissionCompletionService 구현
- [x] 미션 완료 기능을 MissionController에 통합

### 식물 데이터베이스
- [x] PlantEntity 설계
- [x] PlantRepository 구현
- [x] PlantService 구현
- [x] PlantController 구현

### 위치 기반 서비스
- [x] 위치 기반 미션 검색 API 구현

### DTO 구조 최적화
- [x] MissionDto, CompletedPuzzleInfo, UserMissionProgressDto 구현
- [x] MissionCompletionRequest 구현

## 다음 단계

### 파일 업로드
- [ ] File Upload Service 구현 (BASE64 인코딩 이미지 처리 포함)

### 통합 및 테스트
- [ ] 통합 테스트 구현
- [ ] 성능 최적화 및 버그 수정

### 보안 설정
- [ ] Spring Security 설정 재검토 및 구현

## 주요 구현 사항

1. 미션 시스템 개선
   - 미션 CRUD 기능 구현
   - 미션 완료 기능 추가 (퍼즐 조각 완성 개념 도입)
   - 위치 기반 미션 검색 기능 추가

2. 미션 완료 시스템 통합
   - MissionCompletionEntity를 통한 미션 완료 기록 관리
   - MissionController에 미션 완료 기능 통합

3. DTO 구조 최적화
   - 필요한 DTO들을 정의하고 중복 제거
   - 클라이언트-서버 간 효율적인 데이터 전송 구조 마련

4. 패키지 구조 개선
   - 미션 관련 기능을 하나의 패키지로 통합하여 관리 용이성 증대

## 주요 고려사항 및 노트

1. 보안 설정 (Security)
   - 현재 Spring Security 설정이 주석 처리되어 있음
   - 개발 편의를 위해 임시적으로 비활성화된 상태
   - 프로덕션 배포 전 반드시 보안 설정을 재검토하고 활성화해야 함

2. 파일 업로드
   - 현재 이미지를 BASE64 인코딩 문자열로 처리 중
   - 향후 실제 파일 업로드 기능으로 전환 필요 (성능 및 저장 공간 최적화를 위해)

3. 프론트엔드 연동
   - API 엔드포인트 구조가 변경됨 (/api/missions로 통합)
   - 프론트엔드 개발자와 API 변경사항 공유 필요

4. 데이터베이스 설정
   - 현재 개발 환경에서는 인메모리 데이터베이스 사용 중
   - 프로덕션 환경으로 전환 시 실제 데이터베이스 설정 필요

5. 환경 설정
   - 개발, 테스트, 프로덕션 환경별 설정 파일 분리 필요

