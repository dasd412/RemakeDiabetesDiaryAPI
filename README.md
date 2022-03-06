# __Diabetes Diary API Remake__

## 버전 : 1.1.2

***

### 웹 사이트 주소 ###

https://www.diabetes-diary.tk/

### 블로그 주소

https://velog.io/@dasd412/series/%ED%8F%AC%ED%8A%B8%ED%8F%B4%EB%A6%AC%EC%98%A4
***

### 주요 기능

1. 회원 가입 ✅
2. 로그인 (form login과 OAuth login 방식 모두 제공) ✅
3. 일지 작성 및 수정 / 삭제 기능 ✅
4. 달력으로 일지 표시 ✅
5. 공복 혈당 / 식사 혈당/ 평균 혈당 관련 차트 ✅
6. 음식 검색 게시판 ✅
7. 프로필 및 회원 탈퇴 ✅
8. 아이디 ,비밀 번호 찾기 ✅
9. 도커 적용(예정)
10. 시큐어 코딩 (예정)
11. 튜토리얼 기능(예정)
12. ???

***

### API end point

#### Controller

+ IndexController
    + ` GET "/" ` -> 대문 화면
    + ` GET "" `  -> 대문 화면

+ LoginController
    + `GET "/login-form" `
    + `GET "/join-form" `

+ CalendarFormController
    + `GET "/calendar" `

+ ChartFormController
    + `GET "/chart-menu" `
    + `GET "/chart-menu/chart/fasting-plasma-glucose" `
    + `GET "/chart-menu/chart/blood-sugar" `
    + `GET "/chart-menu/chart/average" `
    + `GET "/chart-menu/chart/food-board/list" `

+ DiaryFormController
    + `GET "/post" `
    + `GET "/update-delete/{diaryId}" `
    + `GET "/update-delete/404" ` [circular view path 해결용]

+ ProfileController
    + `GET "/profile/view`

#### REST Controller

+ LoginRestController
    + `POST "/signup/user" `

+ SecurityDiaryRestController
    + `POST "/api/diary/user/diabetes-diary" `
    + `PUT "/api/diary/user/diabetes-diary" `
    + `DELETE "/api/diary/user/diabetes-diary/{diaryId}" `
    + `GET "/api/diary/user/diabetes-diary/list" `

+ SecurityChartRestController
    + `GET "/chart-menu/fasting-plasma-glucose/all" `
    + `GET "/chart-menu/fasting-plasma-glucose/between" `
    + `GET "/chart-menu/blood-sugar/all" `
    + `GET "/chart-menu/blood-sugar/between" `
    + `GET "/chart-menu/average/all" `
    + `GET "/chart-menu/average/between" `

+ ProfileRestController
    + `PUT "/profile/info"`
    + `DELETE /profile/withdrawal"`[회원 탈퇴]
    + `PUT "/profile/password"` [비밀 번호 변경]

+ FindInfoRestController
  + `GET "/user-info/user-name"`
  + `GET "/user-info/password"`

***

### 디렉토리 구조

+ Backend
    + https://github.com/dasd412/RemakeDiabetesDiaryAPI/wiki/backend-directory
+ Frontend
    + https://github.com/dasd412/RemakeDiabetesDiaryAPI/wiki/frontend-directory

***

### 다이어그램

+ ERD
    + ![erd.png](images/erd.png)
+ 브라우저 접근 흐름도
    + ![browserFlow.png](images/browserFlow.png)
+ 배포 다이어그램
    + ![deploy.png](images/deploy.png)

***

### 진행 상황 ###

+ 데이터 재설계 및 JPA 적용
    + DB 스키마 재설계
    + JPA 연관관계 재맵핑
    + JPA Repository 테스트 코드 작성 및 수행
    + 서비스 레이어 생성
    + JPQL @Query 코드 제거 후, QueryDSL 적용. 기존 테스트를 수행하여 정상 작동하는 지 확인하며 진행
    + 컨트롤러 레이어 생성
    + MockMvc 를 활용하여 컨트롤러 레이어 테스트 수행
    + n+1 문제 최적화 수행
    + 삭제 연산 최적화 수행 [벌크 연산 처리]
    + MySql 연동 완료
    + 삽입 연산 최적화 수행
    + 음식 엔티티에 수량 단위 추가 [요구사항 반영]
    + 중복 QueryDSL 코드 BooleanBuilder로 리팩토링. [BooleanBuider를 활용한 동적 쿼리 생성]
    + 프로필 엔티티 추가 및 작성자 엔티티와 1대1 관계 구성


+ 스프링 시큐리티
    + 일반 회원가입과 로그인 구현
    + OAuth 로그인 및 회원 가입 구현
    + 기존 도메인 테스트에 스프링 시큐리티 적용 [관리자만 접근 가능하도록 변경]
    + 도메인 컨트롤러 매핑 url 에서 작성자 정보 제거하고 세션으로 판단하기
    + 회원 탈퇴 기능 구현 [회원 탈퇴 시 서명 필요]
    + 아이디 찾기 , 비밀번호 찾기 구현
    + 시큐어 코딩(예정)


+ 뷰
    + 공통 메뉴 레이아웃 추가
    + 로그인 폼 추가
    + 회원 가입 폼 추가
    + 작성용 폼 생성 및 ajax 로직 추가
    + 수정 / 삭제용 폼 생성 및 ajax 로직 추가
    + 달력과 연동
    + 검색기능 만들고 그 결과로 차트 생성하기
    + 작성용 폼 더 이쁘게 만들기
    + 음식 검색 게시판 만들기
    + 프로필 기능 추가


+ 배포
    + amazon ec2 인스턴스 생성
    + amazon rds 생성 및 연동
    + aws 내에 jar 배포 완료
    + Freenom 과 aws route 53으로 무료 도메인 얻음.
    + acm 에서 무료 ssl 발급 받음
    + 사이트에 https 적용
    + oauth 로그인 리디렉션 url 변경 (크롬 브라우저 등에서 피싱 사이트 의심 발생. 조치 필요.)
    + travis ci 와 code deploy 를 활용하여 배포 자동화 [master branch 푸시하면 자동 배포됨.]
    + travis ci trial plan 만료로 인해 github actions로 바꾸었다.


+ 문서화
    + Readme
    + 코드 내 주석 처리 [JavaDoc 기준]
    + 다이어그램 작성 및 게시
    + dto에 swagger 입히기 (예정)

***

### 테스트 커버리지 ###

+ (2022-03-06 전체 패키지 기준)
    + 테스트 총 175개
    + 클래스 커버리지 93% (128/137)
    + 메소드 커버리지 75% (575/765)
    + 라인 커버리지 74% (1783/2378)


+ (2022-03-06 domain 패키지 기준)
    + 테스트 총 99개
    + 클래스 커버리지 100% (27/27)
    + 메소드 커버리지 85% (160/187)
    + 라인 커버리지 84% (523/622)


+ (2022-03-06 controller 패키지 기준)
    + 테스트 총 76개
    + 클래스 커버리지 93% (80/86)
    + 메소드 커버리지 72% (331/459)
    + 라인 커버리지 68% (846/1243)

***

### 개선 사항

+ 복합키의 장점을 못 살렸음.
+ Nginx 무중단 배포 실패. (아마 elb와 충돌한 것 같다.)
+ 음식 엔티티 저장 로직의 비효율 개선 필요
+ 크롬 브라우저 웹 사이트 보안 인증 및 사이트 보안 강화 필요
***

### 사용 기술

+ SpringBoot
+ JPA
+ Querydsl
+ MySql
+ Spring Security
+ Junit
+ JavaMail
+ Mustache, css
+ JQuery
+ AWS (ec2, route 53, ACM, rds, code deploy )
+ travis ci [trial plan 만료로 인해 사용 중지]
+ github actions

### 사용 라이브러리

+ chart.js
+ datepicker.js
+ bootstrap

***

### 본인 작성이 아닌 것.

+ /resources/static/vendor/* [colorLib 저작권 ]
+ /resources/static/sidebar-07/* [colorLib 저작권 ]
+ /resources/static/js/calendar/ (calendar.js ,formatter.js, stringBuffer.js) [구글링 코드]
+ src/main/java/com/dasd412/remake/api/controller/security/domain_view/FoodPageMaker [스타트 스프링 부트]

