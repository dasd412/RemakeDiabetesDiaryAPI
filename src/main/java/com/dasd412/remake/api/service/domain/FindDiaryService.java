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
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
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
    public List<DiabetesDiary> getDiabetesDiariesOfWriter(EntityId<Writer, Long> writerEntityId) {
        logger.info("getDiabetesDiariesOfWriter");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findDiabetesDiariesOfWriter(writerEntityId.getId());
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

    @Transactional(readOnly = true)
    public List<DiabetesDiary> getDiariesBetweenLocalDateTime(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("get Diaries Between LocalDateTime");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiary(startDate, endDate));
        return diaryRepository.findDiariesWithWhereClause(writerEntityId.getId(), predicates);
    }

    @Transactional(readOnly = true)
    public Double getAverageFpg(EntityId<Writer, Long> writerEntityId) {
        logger.info("getAverageFpg");
        checkNotNull(writerEntityId, "writerId must be provided");
        return diaryRepository.findAverageFpg(writerEntityId.getId(), new ArrayList<>()).orElseThrow(NoResultException::new);
    }

    @Transactional(readOnly = true)
    public Double getAverageFpgBetween(EntityId<Writer, Long> writerEntityId, LocalDateTime startDate, LocalDateTime endDate) {
        logger.info("getAverageFpgBetween");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkArgument(isStartDateEqualOrBeforeEndDate(startDate, endDate), "startDate must be equal or before than endDate");
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideBetweenTimeInDiary(startDate, endDate));
        return diaryRepository.findAverageFpg(writerEntityId.getId(), predicates).orElseThrow(NoResultException::new);
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

    @Transactional(readOnly = true)
    public Profile getProfile(EntityId<Writer, Long> writerEntityId) {
        logger.info("get profile");
        checkNotNull(writerEntityId, "writerId must be provided");

        return writerRepository.findProfile(writerEntityId.getId()).orElseThrow(() -> new NoResultException("해당 프로필이 존재하지 않습니다."));
    }
}
