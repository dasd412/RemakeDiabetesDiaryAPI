# __Diabetes Diary API Remake__
## 포트폴리오 리메이크 

### 진행 상황 ###

+ 데이터 재설계 및 JPA 적용
  + DB 스키마 재설계
  + JPA 연관관계 재맵핑
  + JPA Repository 테스트 코드 작성 및 수행
  + 서비스 레이어 생성
  + JPQL @Query 코드 제거 후, QueryDSL 적용 (기존 테스트를 수행하여 정상 작동하는 지 확인하며 진행)

  
+ 테스트 커버리지 (2021-12-19 refactoringAPI.domain.diary 패키지 기준)
  + 클래스 커버리지 100% (18/18)
  + 메소드 커버리지 80% (104/130)
  + 라인 커버리지 78% (261/332)