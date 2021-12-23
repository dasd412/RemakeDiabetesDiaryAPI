package com.dasd412.remake.api.service;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.writer.Writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


@Service
public class FindDiaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DiaryRepository diaryRepository;
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;

    public FindDiaryService(DiaryRepository diaryRepository, DietRepository dietRepository, FoodRepository foodRepository) {
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }

    /*
    조회용으로만 트랜잭션할 경우 readonly = true 로 하면 조회 성능 향상 가능
     */

    /*
    일지 조회 메서드들
     */
    @Transactional(readOnly = true)
    public Writer getWriterOfDiary(EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("getWriterOfDiary");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        return diaryRepository.findWriterOfDiary(diaryEntityId.getId()).orElseThrow(() -> new NoSuchElementException("해당 일지의 작성자가 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiabetesDiariesOfWriter(EntityId<Writer, Long> writerEntityId) {
        logger.info("getDiabetesDiariesOfWriter");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findDiabetesDiariesOfWriter(writerEntityId.getId());
    }

    @Transactional(readOnly = true)
    public DiabetesDiary getDiabetesDiaryOfWriter(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDiabetesDiaryOfWriter");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diabetesDiaryEntityId.getId()).orElseThrow(() -> new NoSuchElementException("작성자의 일지 중, id에 해당하는 일지가 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesBetweenTime(EntityId<Writer, Long> writerEntityId, String startDateString, String endDateString) {
        logger.info("getDiaryBetweenTime");

        checkNotNull(writerEntityId, "writerId must be provided");

        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(startDate.isBefore(endDate), "startDate must be before than endDate");

            return diaryRepository.findDiaryBetweenTime(writerEntityId.getId(), startDate, endDate);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getFpgHigherOrEqual(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose) {
        logger.info("getFpgHigherOrEqual");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be positive");
        return diaryRepository.findFpgHigherOrEqual(writerEntityId.getId(), fastingPlasmaGlucose);
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getFpgLowerOrEqual(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose) {
        logger.info("getFpgLowerOrEqual");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be positive");
        return diaryRepository.findFpgLowerOrEqual(writerEntityId.getId(), fastingPlasmaGlucose);
    }

    /*
    식단 조회 메서드들
     */
    @Transactional(readOnly = true)
    public List<Diet> getDietsOfDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDietsOfDiary");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return dietRepository.findDietsInDiary(writerEntityId.getId(), diabetesDiaryEntityId.getId());
    }

    @Transactional(readOnly = true)
    public Diet getOneDietOfDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId, EntityId<Diet, Long> dietEntityId) {
        logger.info("get only one diet in diary");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        return dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diabetesDiaryEntityId.getId(), dietEntityId.getId()).orElseThrow(() -> new NoSuchElementException("일지에서 해당 식단이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<Diet> getHigherThanBloodSugarBetweenTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, String startDateString, String endDateString) {
        logger.info("getHigherThanBloodSugarBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(startDate.isBefore(endDate), "startDate must be before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

            return dietRepository.findHigherThanBloodSugarBetweenTime(writerEntityId.getId(), bloodSugar, startDate, endDate);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Diet> getLowerThanBloodSugarBetweenTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, String startDateString, String endDateString) {
        logger.info("getLowerThanBloodSugarBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(startDate.isBefore(endDate), "startDate must be before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

            return dietRepository.findLowerThanBloodSugarBetweenTime(writerEntityId.getId(), bloodSugar, startDate, endDate);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    @Transactional(readOnly = true)
    public List<Diet> getHigherThanBloodSugarInEatTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, EatTime eatTime) {
        logger.info("getHigherThanBloodSugarInEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");
        return dietRepository.findHigherThanBloodSugarInEatTime(writerEntityId.getId(), bloodSugar, eatTime);
    }

    @Transactional(readOnly = true)
    public List<Diet> getLowerThanBloodSugarInEatTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, EatTime eatTime) {
        logger.info("getLowerThanBloodSugarInEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");
        return dietRepository.findLowerThanBloodSugarInEatTime(writerEntityId.getId(), bloodSugar, eatTime);
    }

    @Transactional(readOnly = true)
    public double getAverageBloodSugarOfDiet(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        return dietRepository.findAverageBloodSugarOfDiet(writerEntityId.getId()).orElseThrow(() -> new IllegalStateException("아직 혈당을 기록한 식단이 없습니다."));
    }

    /*
    음식 조회 메서드들
     */
    @Transactional(readOnly = true)
    public List<Food> getFoodsInDiet(EntityId<Writer, Long> writerEntityId, EntityId<Diet, Long> dietEntityId) {
        logger.info("getFoodsInDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        return foodRepository.findFoodsInDiet(writerEntityId.getId(), dietEntityId.getId());
    }

    @Transactional(readOnly = true)
    public Food getOneFoodByIdInDiet(EntityId<Writer, Long> writerEntityId, EntityId<Diet, Long> dietEntityId, EntityId<Food, Long> foodEntityId) {
        logger.info("getOneFoodByIdInDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        checkNotNull(foodEntityId, "foodId must be provided");
        return foodRepository.findOneFoodByIdInDiet(writerEntityId.getId(), dietEntityId.getId(), foodEntityId.getId()).orElseThrow(() -> new NoSuchElementException("식단에서 해당 음식이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<String> getFoodNamesInDietHigherThanBloodSugar(EntityId<Writer, Long> writerEntityId, int bloodSugar) {
        logger.info("getFoodNamesInDietHigherThanBloodSugar");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "bloodSugar must be higher than zero");
        return foodRepository.findFoodNamesInDietHigherThanBloodSugar(writerEntityId.getId(), bloodSugar);
    }

    @Transactional(readOnly = true)
    public List<String> getFoodHigherThanAverageBloodSugarOfDiet(EntityId<Writer, Long> writerEntityId) {
        logger.info("getFoodHigherThanAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        return foodRepository.findFoodHigherThanAverageBloodSugarOfDiet(writerEntityId.getId());
    }

}
