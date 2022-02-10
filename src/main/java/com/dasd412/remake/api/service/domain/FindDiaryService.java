/*
 * @(#)FindDiaryService.java        1.0.7 2022/2/10
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.writer.Writer;

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
import java.util.List;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 조회 비즈니스 로직을 수행하는 서비스 클래스
 *
 * @author 양영준
 * @version 1.0.7 2022년 2월 10일
 */
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


    /**
     * @param diaryEntityId 래퍼로 감싸진 일지 id
     * @return 일지의 작성자
     */
    @Transactional(readOnly = true)
    public Writer getWriterOfDiary(EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("getWriterOfDiary");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        return diaryRepository.findWriterOfDiary(diaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 작성자가 작성한 모든 혈당 일지
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiabetesDiariesOfWriter(EntityId<Writer, Long> writerEntityId) {
        logger.info("getDiabetesDiariesOfWriter");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findDiabetesDiariesOfWriter(writerEntityId.getId());
    }

    /**
     * @param writerEntityId        래퍼로 감싸진 작성자 id
     * @param diabetesDiaryEntityId 래퍼로 감싸진 일지 id
     * @return 입력에 해당하는 혈당 일지 하나
     */
    @Transactional(readOnly = true)
    public DiabetesDiary getDiabetesDiaryOfWriter(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDiabetesDiaryOfWriter");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diabetesDiaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자의 일지 중, id에 해당하는 일지가 없습니다."));
    }

    /**
     * @param writerEntityId        래퍼로 감싸진 작성자 id
     * @param diabetesDiaryEntityId 래퍼로 감싸진 일지 id
     * @return 입력에 해당하는 혈당 일지 하나 + 관련된 하위 엔티티 모두
     */
    @Transactional(readOnly = true)
    public DiabetesDiary getDiabetesDiaryOfWriterWithRelation(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diabetesDiaryEntityId) {
        logger.info("getDiabetesDiaryOfWriterWithRelation");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diabetesDiaryEntityId, "diaryId must be provided");
        return diaryRepository.findDiabetesDiaryOfWriterWithRelation(writerEntityId.getId(), diabetesDiaryEntityId.getId()).orElseThrow(() -> new NoResultException("작성자의 일지 중, id에 해당하는 일지가 없습니다."));
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 전체 기간 동안 작성한 일지들 + 하위 엔티티 모두 (fetch join 되어 있기 때문에 하위 엔티티 참조해도 무방하다.)
     */
    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiabetesDiariesOfWriterWithRelation(EntityId<Writer, Long> writerEntityId) {
        logger.info("getDiabetesDiariesOfWriterWithRelation");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findDiabetesDiariesOfWriterWithRelation(writerEntityId.getId());
    }

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesWithRelationBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getDiariesWithRelationBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before than endDate");
        return diaryRepository.findDiariesWithRelationBetweenTime(writerEntityId.getId(), startDate, endDate);
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

            checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before than endDate");

            return diaryRepository.findDiaryBetweenTime(writerEntityId.getId(), startDate, endDate);
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
        checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before than endDate");
        return diaryRepository.findDiaryBetweenTime(writerEntityId.getId(), startDate, endDate);
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
        return diaryRepository.findFpgHigherOrEqual(writerEntityId.getId(), fastingPlasmaGlucose);
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
        return diaryRepository.findFpgLowerOrEqual(writerEntityId.getId(), fastingPlasmaGlucose);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 전체 기간 내 평균 혈당
     */
    @Transactional(readOnly = true)
    public Double getAverageFpg(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageFpg");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findAverageFpg(writerEntityId.getId()).orElseThrow(NoResultException::new);
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
        checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before than endDate");
        return diaryRepository.findAverageFpgBetweenTime(writerEntityId.getId(), startDate, endDate).orElseThrow(NoResultException::new);
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

            checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or  before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

            return dietRepository.findHigherThanBloodSugarBetweenTime(writerEntityId.getId(), bloodSugar, startDate, endDate);
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

            checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before endDate");
            checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");

            return dietRepository.findLowerThanBloodSugarBetweenTime(writerEntityId.getId(), bloodSugar, startDate, endDate);
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
        return dietRepository.findHigherThanBloodSugarInEatTime(writerEntityId.getId(), bloodSugar, eatTime);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param bloodSugar     식사 혈당
     * @param eatTime        식사 시간
     * @return 입력된 식사 시간 중, 식사 혈당 입력 값보다 낮거나 같은 식단들
     */
    @Transactional(readOnly = true)
    public List<Diet> getLowerThanBloodSugarInEatTime(EntityId<Writer, Long> writerEntityId, int bloodSugar, EatTime eatTime) {
        logger.info("getLowerThanBloodSugarInEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "blood sugar must be higher than zero");
        return dietRepository.findLowerThanBloodSugarInEatTime(writerEntityId.getId(), bloodSugar, eatTime);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 전체 기간 동안의 작성자의 평균 식사 혈당
     */
    @Transactional(readOnly = true)
    public double getAverageBloodSugarOfDiet(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        return dietRepository.findAverageBloodSugarOfDiet(writerEntityId.getId()).orElseThrow(() -> new IllegalStateException("아직 혈당을 기록한 식단이 없습니다."));
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param startDate      시작 날짜
     * @param endDate        끝 날짜
     * @return 해당 기간 동안의 작성자의 평균 식사 혈당
     */
    @Transactional(readOnly = true)
    public double getAverageBloodSugarOfDietBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageBloodSugarOfDietBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before endDate");
        return dietRepository.findAverageBloodSugarOfDietBetweenTime(writerEntityId.getId(), startDate, endDate).orElseThrow(NoResultException::new);
    }


    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 전체 기간 동안의 (평균 혈당, 식사 시간) 형태의 튜플들
     */
    @Transactional(readOnly = true)
    public List<Tuple> getAverageBloodSugarGroupByEatTime(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageBloodSugarGroupByEatTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        return dietRepository.findAverageBloodSugarGroupByEatTime(writerEntityId.getId());
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param startDate      시작 날짜
     * @param endDate        끝 날짜
     * @return 해당 기간 동안의 (평균 혈당, 식사 시간) 형태의 튜플들
     */
    @Transactional(readOnly = true)
    public List<Tuple> getAverageBloodSugarGroupByEatTimeBetweenTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageBloodSugarGroupByEatTimeBetweenTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(startDate.isBefore(endDate) || startDate.isEqual(endDate), "startDate must be equal or before endDate");
        return dietRepository.findAverageBloodSugarGroupByEatTimeBetweenTime(writerEntityId.getId(), startDate, endDate);
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

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param dietEntityId   래퍼로 감싸진 식단 id
     * @param foodEntityId   래퍼로 감싸진 음식 id
     * @return 입력에 해당 하는 음식 하나
     */
    @Transactional(readOnly = true)
    public Food getOneFoodByIdInDiet(EntityId<Writer, Long> writerEntityId, EntityId<Diet, Long> dietEntityId, EntityId<Food, Long> foodEntityId) {
        logger.info("getOneFoodByIdInDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        checkNotNull(foodEntityId, "foodId must be provided");
        return foodRepository.findOneFoodByIdInDiet(writerEntityId.getId(), dietEntityId.getId(), foodEntityId.getId()).orElseThrow(() -> new NoResultException("식단에서 해당 음식이 존재하지 않습니다."));
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param bloodSugar     식사 혈당
     * @return 식단 혈당 입력보다 높거나 같은 식단들에 기재된 음식들
     */
    @Transactional(readOnly = true)
    public List<String> getFoodNamesInDietHigherThanBloodSugar(EntityId<Writer, Long> writerEntityId, int bloodSugar) {
        logger.info("getFoodNamesInDietHigherThanBloodSugar");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(bloodSugar > 0, "bloodSugar must be higher than zero");
        return foodRepository.findFoodNamesInDietHigherThanBloodSugar(writerEntityId.getId(), bloodSugar);
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @return 작성자의 평균 혈당보다 높거나 같은 식단들에 기재된 음식들
     */
    @Transactional(readOnly = true)
    public List<String> getFoodHigherThanAverageBloodSugarOfDiet(EntityId<Writer, Long> writerEntityId) {
        logger.info("getFoodHigherThanAverageBloodSugarOfDiet");
        checkNotNull(writerEntityId, "writerId must be provided");
        return foodRepository.findFoodHigherThanAverageBloodSugarOfDiet(writerEntityId.getId());
    }

    /**
     * @param writerEntityId 래퍼로 감싸진 작성자 id
     * @param predicates     where 절의 조건문들
     * @param pageable       페이징 객체
     * @return 조건을 만족하는 페이지에 해당하는 dto (음식 이름, 식사 혈당, 작성 시간)
     */
    @Transactional(readOnly = true)
    public Page<FoodBoardDTO> getFoodByPagination(EntityId<Writer, Long> writerEntityId, List<Predicate> predicates, Pageable pageable) {
        logger.info("getFoodByPagination");
        checkNotNull(writerEntityId, "writerId must be provided");
        return foodRepository.findFoodsWithPagination(writerEntityId.getId(), predicates, pageable);
    }


}
