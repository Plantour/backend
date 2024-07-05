# BACKEND

# Plantour 프로젝트 진행 상황

## 완료된 작업

### 사용자 관리
- [x] UserEntity 설계
- [x] UserRepository 구현
- [x] UserService 구현
- [x] UserController 구현
- [x] Security Config 설정

### 미션 시스템
- [x] MissionEntity 설계
- [x] MissionRepository 구현
- [x] MissionService 구현
- [x] MissionController 구현

### 식물 데이터베이스
- [x] PlantEntity 설계
- [x] PlantRepository 구현
- [x] PlantService 구현
- [x] PlantController 구현

### 위치 기반 서비스
- [x] 위치 기반 미션 검색 API 구현

## 다음 단계

### 게시물 기능
- [ ] PostEntity 설계
- [ ] PostRepository 구현
- [ ] PostService 구현
- [ ] PostController 구현

### 파일 업로드
- [ ] File Upload Service 구현

### 통합 및 테스트
- [ ] 통합 테스트
- [ ] 버그 수정 및 최적화

## 주요 구현 사항

1. 사용자 관리 시스템 구축
    - 회원가입, 로그인 기능 구현
    - Spring Security를 이용한 보안 설정

2. 미션 시스템 구현
    - 미션 CRUD 기능 구현
    - 위치 기반 미션 검색 기능 추가

3. 식물 데이터베이스 구축
    - 식물 정보 CRUD 기능 구현
    - 다양한 조건(이름, 학명, 계절, 특징)으로 식물 검색 기능 추가

4. 위치 기반 서비스 기초 구현
    - 주변 미션 검색 API 구현
    - 프론트엔드와의 연동을 위한 기반 마련

## 다음 단계 주요 고려사항

1. 게시물 기능 구현
    - 사용자가 미션 수행 결과를 게시할 수 있는 기능 개발
    - 미션, 식물, 사용자와의 연관 관계 설정

2. 파일 업로드 기능 구현
    - 사용자가 찍은 식물 사진을 안전하게 업로드하고 저장하는 기능 개발

3. 통합 테스트 및 최적화
    - 각 기능 간의 통합 테스트 수행
    - 성능 최적화 및 버그 수정

4. 프론트엔드와의 연동
    - RESTful API 문서화
    - 프론트엔드 개발자와의 협업을 위한 인터페이스 정의
