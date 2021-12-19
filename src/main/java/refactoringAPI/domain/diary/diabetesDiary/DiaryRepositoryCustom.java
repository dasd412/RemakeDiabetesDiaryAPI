package refactoringAPI.domain.diary.diabetesDiary;

import refactoringAPI.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DiaryRepositoryCustom {

    Long findCountOfId();

    Long findMaxOfId();

    Optional<Writer> findWriterOfDiary(Long diaryId);

    List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId);

    Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId);

    List<DiabetesDiary> findDiaryBetweenTime(Long writerId, LocalDateTime startDate, LocalDateTime endDate);

    List<DiabetesDiary> findFpgHigherOrEqual(Long writerId, int fastingPlasmaGlucose);

    List<DiabetesDiary> findFpgLowerOrEqual(Long writerId, int fastingPlasmaGlucose);
}
