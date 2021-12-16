package jpaEx.domain.diary.diet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietRepository extends JpaRepository<Diet, Long> {
        /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */

    @Query(value = "SELECT COUNT(di.dietId) FROM Diet di")
    Long findCountOfId();

    @Query(value = "SELECT MAX(di.dietId) FROM Diet di")
    Long findMaxOfId();

    //작성자의 혈당 일지 내 모든 식단 조회
    @Query(value = "SELECT diet FROM Diet diet INNER JOIN diet.diary diary WHERE diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id")
    List<Diet> findDietsInDiary(@Param("writer_id")Long writerId,@Param("diary_id") Long diaryId);

    //작성자의 혈당 일지 내 특정 식단 조회
    @Query(value="SELECT diet FROM Diet diet INNER JOIN diet.diary diary WHERE diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id AND  diet.dietId = :diet_id")
    Optional<Diet>findOneDietInDiary(@Param("writer_id")Long writerId,@Param("diary_id") Long diaryId,@Param("diet_id")Long DietId);

    //특정 기간 내 일정 혈당 수치 이상의 모든 식단 조회
//    @Query(value="")
//    List<Diet>findHigherThanBloodSugarBetweenTime(@Param("writer_id")Long writerId, @Param("blood_sugar")int bloodSugar, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    //식사 시간이 oo 이고 혈당 수치 일정 이상인 것들 조회



}
