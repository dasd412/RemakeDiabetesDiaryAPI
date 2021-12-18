package refactoringAPI.domain.diary.diabetesDiary;

import org.springframework.data.repository.query.Param;
import refactoringAPI.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryRepositoryCustom {

    Long findCountOfId();

    Long findMaxOfId();

    Optional<Writer> findWriterOfDiary(@Param("diary_id") Long diaryId);

    List<DiabetesDiary> findDiabetesDiariesOfWriter(@Param("writer_id") Long writerId);

    Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(@Param("writer_id") Long writerId, @Param("diary_id") Long diaryId);

    List<DiabetesDiary> findDiaryBetweenTime(@Param("writer_id") Long writerId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    List<DiabetesDiary> findFpgHigherOrEqual(@Param("writer_id") Long writerId, @Param("bloodSugar") int fastingPlasmaGlucose);

    List<DiabetesDiary> findFpgLowerOrEqual(@Param("writer_id") Long writerId, @Param("bloodSugar") int fastingPlasmaGlucose);
}
