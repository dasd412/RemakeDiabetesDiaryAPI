/*
 * @(#)FindDiaryService.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.controller.security.domain_view.FoodPageVO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.InequalitySign;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.writer.Writer;

import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


import static com.dasd412.remake.api.domain.diary.PredicateMaker.*;
import static com.dasd412.remake.api.util.DateStringConverter.isStartDateEqualOrBeforeEndDate;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class FindDiaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DiaryRepository diaryRepository;
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;
    private final WriterRepository writerRepository;

    public FindDiaryService(DiaryRepository diaryRepository, DietRepository dietRepository, FoodRepository foodRepository, WriterRepository writerRepository) {
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
        this.writerRepository = writerRepository;
    }

    @Transactional(readOnly = true)
    public Writer getWriterOfDiary(EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("getWriterOfDiary");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        return diaryRepository.findWriterOfDiary(diaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
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
        return diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diabetesDiaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자의 일지 중, id에 해당하는 일지가 없습니다."));
    }

    @Transactional(readOnly = true)
    public DiabetesDiary getDiabetesDiaryWithSubEntitiesOfWriter(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDiabetesDiaryOfWriterWithRelation");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return diaryRepository.findDiabetesDiaryWithSubEntitiesOfWriter(writerEntityId.getId(), diabetesDiaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자의 일지 중, id에 해당하는 일지가 없습니다."));
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiabetesDiariesWithSubEntitiesOfWriter(EntityId<Writer, Long> writerEntityId) {
        logger.info("getDiabetesDiariesOfWriterWithRelation");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findDiabetesDiariesWithSubEntitiesOfWriter(writerEntityId.getId(), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesWithRelationBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getDiariesWithRelationBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiary(startDate, endDate));
        return diaryRepository.findDiabetesDiariesWithSubEntitiesOfWriter(writerEntityId.getId(), predicates);
    }

    /**
     * String parameter 로 올 경우 처리하는 메서드
     *
     * @param writerEntityId  래퍼로 감싸진 작성자 id
     * @param startDateString 시작 날짜 문자열
     * @param endDateString   끝 날짜 문자열
     * @return 해당 기간에 작성된 일지들 (하위 엔티티는 포함하지 않는다. n+1 발생하지 않도록, 이 메서드의 리턴 값에서는 하위 엔티티를 참조 하면 안된다.)
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesBetweenTime(EntityId<Writer, Long> writerEntityId, String startDateString, String endDateString) {
        logger.info("getDiaryBetweenTime");

        checkNotNull(writerEntityId, "writerId must be provided");

        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(decideBetweenTimeInDiary(startDate, endDate));

            return diaryRepository.findDiariesWithWhereClause(writerEntityId.getId(), predicates);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    /**
     * LocalDateTime 파라미터로 올 경우 처리하는 메서드
     *
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param startDate      시작 날짜
     * @param endDate        끝 날짜
     * @return 해당 기간에 작성된 일지들
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesBetweenLocalDateTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("get Diaries Between LocalDateTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiary(startDate, endDate));
        return diaryRepository.findDiariesWithWhereClause(writerEntityId.getId(), predicates);
    }


    /**
     * @param writerEntityId       래퍼로 감싸진 작성자 id
     * @param fastingPlasmaGlucose 공복 혈당
     * @return 입력된 공복 혈당보다 높거나 같은 일지들
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getFpgHigherOrEqual(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose) {
        logger.info("getFpgHigherOrEqual");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be positive");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySignOfFastingPlasmaGlucose(InequalitySign.GREAT_OR_EQUAL, fastingPlasmaGlucose));
        return diaryRepository.findDiariesWithWhereClause(writerEntityId.getId(), predicates);
    }

    /**
     * @param writerEntityId       래퍼로 감싸진 작성자 id
     * @param fastingPlasmaGlucose 공복 혈당
     * @return 입력된 공복 혈당보다 낮거나 같은 일지들
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getFpgLowerOrEqual(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose) {
        logger.info("getFpgLowerOrEqual");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be positive");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySignOfFastingPlasmaGlucose(InequalitySign.LESSER_OR_EQUAL, fastingPlasmaGlucose));
        return diaryRepository.findDiariesWithWhereClause(writerEntityId.getId(), predicates);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 전체 기간 내 평균 혈당
     */
    @Transactional(readOnly = true)
    public Double getAverageFpg(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageFpg");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findAverageFpg(writerEntityId.getId(), new ArrayList<>()).orElseThrow(NoResultException::new);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param startDate      시작 날짜
     * @param endDate        끝 날짜
     * @return 해당 기간 내 평균 혈당
     */
    @Transactional(readOnly = true)
    public Double getAverageFpgBetween(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageFpgBetween");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiary(startDate, endDate));
        return diaryRepository.findAverageFpg(writerEntityId.getId(), predicates).orElseThrow(NoResultException::new);
    }

    /**
     * @param writerEntityId        래퍼로 감싸진 작성자 id
     * @param diabetesDiaryEntityId 래퍼로 감싸진 일지 id
     * @return 입력 일지 내 식단들
     */
    @Transactional(readOnly = true)
    public List<Diet> getDietsOfDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDietsOfDiary");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return dietRepository.findDietsInDiary(writerEntityId.getId(), diabetesDiaryEntityId.getId());
    }

    /**
     * @param writerEntityId        래퍼로 감싸진 작성자 id
     * @param diabetesDiaryEntityId 래퍼로 감싸진 일지 id
     * @param dietEntityId          래퍼로 감싸진 식단 id
     * @return 입력에 해당하는 식단 1개
     */
    @Transactional(readOnly = true)
    public Diet getOneDietOfDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId, EntityId<Diet, Long> dietEntityId) {
        logger.info("get only one diet in diary");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        return dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diabetesDiaryEntityId.getId(), dietEntityId.getId()).orElseThrow(() -> new NoResultException("일지에서 해당 식단이 존재하지 않습니다."));
    }

    /**
     * @param writerEntityId  래퍼로 감싸진 작성자 id
     * @param bloodSugar      식사 혈당
     * @param startDateString 시작 날짜 문자열
     * @param endDateString   끝 날짜 문자열
     * @return 기간 내에 식사 혈당 입력값보다 높거나 같은 식단들
     */
    @Transactional(readOnly = true)
    public List<Diet> getHigherThanBloodSugarBetweenTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, String startDateString, String endDateString) {
        logger.info("getHigherThanBloodSugarBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or  before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

            List<Predicate> predicates = new ArrayList<>();
            predicates.add(decideEqualitySignOfBloodSugar(InequalitySign.GREAT_OR_EQUAL, bloodSugar));
            predicates.add(decideBetweenTimeInDiet(startDate, endDate));
            return dietRepository.findDietsWithWhereClause(writerEntityId.getId(), predicates);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    /**
     * @param writerEntityId  래퍼로 감싸진 작성자 id
     * @param bloodSugar      식사 혈당
     * @param startDateString 시작 날짜 문자열
     * @param endDateString   끝 날짜 문자열
     * @return 기간 내에 식사 혈당 입력값보다 낮거나 같은 식단들
     */
    @Transactional(readOnly = true)
    public List<Diet> getLowerThanBloodSugarBetweenTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, String startDateString, String endDateString) {
        logger.info("getLowerThanBloodSugarBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        try {
            LocalDateTime startDate = LocalDateTime.parse(startDateString);
            LocalDateTime endDate = LocalDateTime.parse(endDateString);

            checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(decideEqualitySignOfBloodSugar(InequalitySign.LESSER_OR_EQUAL, bloodSugar));
            predicates.add(decideBetweenTimeInDiet(startDate, endDate));
            return dietRepository.findDietsWithWhereClause(writerEntityId.getId(), predicates);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("LocalDateTime 포맷으로 변경할 수 없는 문자열입니다.");
        }
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param bloodSugar     식사 혈당
     * @param eatTime        식사 시간
     * @return 입력된 식사 시간 중, 식사 혈당 입력 값보다 높거나 같은 식단들
     */
    @Transactional(readOnly = true)
    public List<Diet> getHigherThanBloodSugarInEatTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, EatTime eatTime) {
        logger.info("getHigherThanBloodSugarInEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySignOfBloodSugar(InequalitySign.GREAT_OR_EQUAL, bloodSugar));
        predicates.add(decideEatTime(eatTime));
        return dietRepository.findDietsWithWhereClause(writerEntityId.getId(), predicates);
    }

    @Transactional(readOnly = true)
    public List<Diet> getLowerThanBloodSugarInEatTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, EatTime eatTime) {
        logger.info("getLowerThanBloodSugarInEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySignOfBloodSugar(InequalitySign.LESSER_OR_EQUAL, bloodSugar));
        predicates.add(decideEatTime(eatTime));
        return dietRepository.findDietsWithWhereClause(writerEntityId.getId(), predicates);
    }

    @Transactional(readOnly = true)
    public double getAverageBloodSugarOfDietWithWhereClause(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        return dietRepository.findAverageBloodSugarOfDietWithWhereClause(writerEntityId.getId(), new ArrayList<>()).orElseThrow(() -> new IllegalStateException("아직 혈당을 기록한 식단이 없습니다."));
    }

    @Transactional(readOnly = true)
    public double getAverageBloodSugarOfDietBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageBloodSugarOfDietBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before endDate");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiet(startDate, endDate));
        return dietRepository.findAverageBloodSugarOfDietWithWhereClause(writerEntityId.getId(), predicates).orElseThrow(NoResultException::new);
    }

    @Transactional(readOnly = true)
    public List<Tuple> getAverageBloodSugarWithWhereClauseGroupByEatTime(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageBloodSugarGroupByEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        return dietRepository.findAverageBloodSugarWithWhereClauseGroupByEatTime(writerEntityId.getId(), new ArrayList<>());
    }

    @Transactional(readOnly = true)
    public List<Tuple> getAverageBloodSugarGroupByEatTimeBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageBloodSugarGroupByEatTimeBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before endDate");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiet(startDate, endDate));
        return dietRepository.findAverageBloodSugarWithWhereClauseGroupByEatTime(writerEntityId.getId(), predicates);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param dietEntityId   래퍼로 감싸진 식단 id
     * @return 입력 식단 내 모든 음식들
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
        return foodRepository.findOneFoodByIdInDiet(writerEntityId.getId(), dietEntityId.getId(), foodEntityId.getId()).orElseThrow(() -> new NoResultException("식단에서 해당 음식이 존재하지 않습니다."));
    }

    @Transactional(readOnly = true)
    public List<String> getFoodNamesInDietHigherThanBloodSugar(EntityId<Writer, Long> writerEntityId, int bloodSugar) {
        logger.info("getFoodNamesInDietHigherThanBloodSugar");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "bloodSugar must be higher than zero");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySignOfBloodSugar(InequalitySign.GREAT_OR_EQUAL, bloodSugar));
        return foodRepository.findFoodNamesInDietWithWhereClause(writerEntityId.getId(), predicates);
    }

    @Transactional(readOnly = true)
    public List<String> getFoodHigherThanAverageBloodSugarOfDiet(EntityId<Writer, Long> writerEntityId) {
        logger.info("getFoodHigherThanAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideAverageOfDiet(InequalitySign.GREAT_OR_EQUAL));
        return foodRepository.findFoodNamesInDietWithWhereClause(writerEntityId.getId(), predicates);
    }

    @Transactional(readOnly = true)
    public Page<FoodBoardDTO> getFoodByPagination(EntityId<Writer, Long> writerEntityId, FoodPageVO foodPageVO) {
        logger.info("getFoodByPagination");
        checkNotNull(writerEntityId, "writerId must be provided");

        Pageable page = foodPageVO.makePageable();
        logger.info("page vo : " + page.toString());

        /* where 절 이후에 쓰이는 조건문 */
        List<Predicate> predicates = new ArrayList<>();

        if (foodPageVO.getSign() != null && foodPageVO.getEnumOfSign() != InequalitySign.NONE) {
            predicates.add(decideEqualitySignOfBloodSugar(foodPageVO.getEnumOfSign(), foodPageVO.getBloodSugar()));
        }


        if (isStartDateEqualOrBeforeEndDate(foodPageVO.convertStartDate(), foodPageVO.convertEndDate())) {        /* 날짜 규격에 적합한 파라미터라면, where 절에 추가해준다. */
            predicates.add(decideBetweenTimeInDiary(foodPageVO.convertStartDate(), foodPageVO.convertEndDate()));
        }

        return foodRepository.findFoodsWithPaginationAndWhereClause(writerEntityId.getId(), predicates, page);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 작성자와 1 대 1 관계의 프로필 정보
     */
    @Transactional(readOnly = true)
    public Profile getProfile(EntityId<Writer, Long> writerEntityId) {
        logger.info("get profile");
        checkNotNull(writerEntityId, "writerId must be provided");

        return writerRepository.findProfile(writerEntityId.getId()).orElseThrow(() -> new NoResultException("해당 프로필이 존재하지 않습니다."));
    }
}
