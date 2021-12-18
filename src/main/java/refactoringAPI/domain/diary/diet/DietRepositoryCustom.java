package refactoringAPI.domain.diary.diet;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DietRepositoryCustom {

    Long findCountOfId();

    Long findMaxOfId();

    List<Diet> findDietsInDiary(Long writerId, Long diaryId);

    Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long DietId);

    List<Diet> findHigherThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate);

    List<Diet> findLowerThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate);

    List<Diet> findHigherThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime);

    List<Diet> findLowerThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime);

    double findAverageBloodSugarOfDiet(Long writerId);
}
