package jpaEx.domain.diary.diet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    //todo 복합키 조인 필요
    //내부조인을 홯용해서 혈당 일지 내 모든 식단 조회
    @Query(value = "SELECT di FROM DiabetesDiary as d INNER JOIN d.dietList as di WHERE di.diary = : diaryId")
    List<Diet> findDietInDiary(@Param("diaryId") Long id);

    //내부 조인 + COUNT 를 활용해 혈당 일지 내 식단 개수 조회
}
