/*
 * @(#)UpdateDeleteDiaryService.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryUpdateDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodForUpdateDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.persistence.NoResultException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class UpdateDeleteDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FoodRepository foodRepository;
    private final DietRepository dietRepository;
    private final DiaryRepository diaryRepository;
    private final WriterRepository writerRepository;

    private final SaveDiaryService saveDiaryService;

    public UpdateDeleteDiaryService(FoodRepository foodRepository, DietRepository dietRepository, DiaryRepository diaryRepository, WriterRepository writerRepository, SaveDiaryService saveDiaryService) {
        this.foodRepository = foodRepository;
        this.dietRepository = dietRepository;
        this.diaryRepository = diaryRepository;
        this.writerRepository = writerRepository;
        this.saveDiaryService = saveDiaryService;
    }

    @Transactional
    public Long updateDiaryWithEntities(PrincipalDetails principalDetails, SecurityDiaryUpdateDTO dto) {
        logger.info("update diary in service logic");
        checkNotNull(principalDetails, "principalDetails must be provided");

        /* 0. 현재 세션에 담긴 사용자 정보 판별 */
        Writer writer = writerRepository.findById(principalDetails.getWriter().getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));

        /* 1. 혈당 일지 변경 감지 되었으면 수정. */
        Long diabetesDiaryId = dto.getDiaryId();
        if (dto.isDiaryDirty()) {
            DiabetesDiary targetDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(principalDetails.getWriter().getId(), diabetesDiaryId)
                    .orElseThrow(() -> new NoResultException("해당 혈당일지가 존재하지 않습니다."));

            targetDiary.update(dto.getFastingPlasmaGlucose(), dto.getRemark());
        }

        /* 2. 아침 식단 변경 감지 되었으면 수정 */
        Long breakFastId = dto.getBreakFastId();

        Diet targetBreakFast = dietRepository.findOneDietByIdInDiary(principalDetails.getWriter().getId(), diabetesDiaryId, breakFastId)
                .orElseThrow(() -> new NoResultException("해당 아침 식단이 존재하지 않습니다."));
        if (dto.isBreakFastDirty()) {
            targetBreakFast.update(EatTime.BreakFast, dto.getBreakFastSugar());
        }

        /* 3. 점심 식단 변경 감지 되었으면 수정 */
        Long lunchId = dto.getLunchId();

        Diet targetLunch = dietRepository.findOneDietByIdInDiary(principalDetails.getWriter().getId(), diabetesDiaryId, lunchId)
                .orElseThrow(() -> new NoResultException("해당 점심 식단이 존재하지 않습니다."));
        if (dto.isLunchDirty()) {
            targetLunch.update(EatTime.Lunch, dto.getLunchSugar());
        }

        /* 4. 저녁 식단 변경 감지 되었으면 수정 */
        Long dinnerId = dto.getDinnerId();

        Diet targetDinner = dietRepository.findOneDietByIdInDiary(principalDetails.getWriter().getId(), diabetesDiaryId, dinnerId)
                .orElseThrow(() -> new NoResultException("해당 저녁 식단이 존재하지 않습니다."));

        if (dto.isDinnerDirty()) {
            targetDinner.update(EatTime.Dinner, dto.getDinnerSugar());
        }

        /* 5. 기존 음식 엔티티 삭제 ( in (id) 벌크 삭제) */
        List<SecurityFoodForUpdateDTO> allOldFoods = new ArrayList<>();
        allOldFoods.addAll(dto.getOldBreakFastFoods());
        allOldFoods.addAll(dto.getOldLunchFoods());
        allOldFoods.addAll(dto.getOldDinnerFoods());

        if (allOldFoods.size() > 0) {
            List<EntityId<Food, Long>> foodEntityIds = allOldFoods.stream().map(elem -> EntityId.of(Food.class, elem.getId())).collect(Collectors.toList());
            bulkDeleteFoods(foodEntityIds);
        }

        /* 6. 음식 엔티티 새로이 생성 */
        dto.getNewBreakFastFoods()
                .forEach(elem -> {
                    Food food = new Food(saveDiaryService.getNextIdOfFood(), targetBreakFast, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
                    targetBreakFast.addFood(food);
                });

        dto.getNewLunchFoods()
                .forEach(elem -> {
                    Food food = new Food(saveDiaryService.getNextIdOfFood(), targetLunch, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
                    targetLunch.addFood(food);
                });

        dto.getNewDinnerFoods()
                .forEach(elem -> {
                    Food food = new Food(saveDiaryService.getNextIdOfFood(), targetDinner, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
                    targetDinner.addFood(food);
                });

        writerRepository.save(writer);

        return diabetesDiaryId;
    }

    @Transactional
    public void deleteDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("delete diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 없습니다."));

        DiabetesDiary targetDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoResultException("해당 혈당일지가 존재하지 않습니다."));

        logger.info("association detached");
        writer.removeDiary(targetDiary);

        logger.info("bulk delete diary");
        diaryRepository.bulkDeleteDiary(diaryEntityId.getId());
    }

    @Transactional
    public Profile updateProfile(EntityId<Writer, Long> writerEntityId, DiabetesPhase phase) {
        logger.info("update profile");
        checkNotNull(writerEntityId, "writerId must be provided");

        Profile targetProfile = writerRepository.findProfile(writerEntityId.getId()).orElseThrow(() -> new NoResultException("프로필이 존재하지 않습니다."));

        targetProfile.modifyDiabetesPhase(phase);

        return targetProfile;
    }

    @Transactional
    public void bulkDeleteFoods(List<EntityId<Food, Long>> foodEntityIds) {
        logger.info("bulk delete food service");
        List<Long> foodIds = foodEntityIds.stream().map(EntityId::getId).collect(Collectors.toList());
        foodRepository.bulkDeleteFood(foodIds);
    }
}
