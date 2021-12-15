package jpaEx.domain.diary.diabetesDiary;

import jpaEx.domain.diary.writer.Writer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<DiabetesDiary, Long> {
    /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */

    @Query(value = "SELECT COUNT(d.diaryId) FROM DiabetesDiary d")
    Long findCountOfId();

    @Query(value = "SELECT MAX(d.diaryId) FROM DiabetesDiary d")
    Long findMaxOfId();

    @Query(value = "SELECT diary.writer FROM DiabetesDiary diary WHERE diary.diaryId= :diary_id")
    Optional<Writer> findWriterOfDiary(@Param("diary_id") Long diaryId);

    //작성자와 관련된 일지들 조회
    @Query(value = "FROM DiabetesDiary diary INNER JOIN Writer writer WHERE diary.writer.id = :writer_id")
    List<DiabetesDiary> findDiabetesDiariesOfWriter(@Param("writer_id") Long writerId);

    //작성자와 관련된 "특정 id"의 일지 조회

    //Optional<DiabetesDiary> findDiabetesDiaryOfWriter(@Param("writer_id") Long writerId, @Param("diary_id") Long diaryId);

}
