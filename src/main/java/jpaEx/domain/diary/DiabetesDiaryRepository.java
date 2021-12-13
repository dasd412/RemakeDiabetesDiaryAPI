package jpaEx.domain.diary;

import jpaEx.domain.diet.Diet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiabetesDiaryRepository extends JpaRepository<DiabetesDiary,Long> {

    /*
    JPQL 에서 SELECT 절에 조회할 대상을 지정하는 것을 프로젝션(projection)이라 한다.
    SELECT 프로젝션 대상 FROM 으로 대상을 선택한다.
    엔티티, 임베디드 타입, 스칼라 타입이 프로젝션 대상으로 될 수 있다.
     */

    /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */

    //시작일 ~ 종료일 사이의 혈당일지 모두 조회
    @Query(value="SELECT d FROM DiabetesDiary as d WHERE d.writtenTime BETWEEN :startDate AND :endDate")
    List<DiabetesDiary>findDiaryBetweenTime(@Param("startDate")LocalDateTime startDate, @Param("endDate")LocalDateTime endDate);

    //입력된 공복혈당 보다 높은 공복혈당을 기록한 일지 모두 조회
    @Query(value="SELECT d FROM DiabetesDiary as d WHERE d.fastingPlasmaGlucose >= : bloodSugar")
    List<DiabetesDiary>findDiaryHigherThan(@Param("bloodSugar")int fastingPlasmaGlucose);

    //내부조인을 홯용해서 혈당 일지 내 모든 식단 조회
    @Query(value="SELECT di FROM DiabetesDiary as d INNER JOIN d.dietList as di WHERE di.diary = : diaryId")
    List<Diet>findDietInDiary(@Param("diaryId")Long id);

    //내부 조인 + COUNT 를 활용해 혈당 일지 내 식단 개수 조회

    //내부 조인을 활용해 혈당 일지 내 식단 내 음식들 조회

    //내부 조인 + COUNT 를 활용해 혈당 일지 내 식단 내 음식 개수 조회


}
