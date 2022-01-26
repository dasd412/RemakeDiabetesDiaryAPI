/*
 * @(#)SecurityDiaryRestController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.*;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.dasd412.remake.api.service.domain.UpdateDeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 로그인한 사용자들이 일지를 "사용" (작성, 수정, 삭제 등...)할 때의 일을 처리하는 RestController
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@RestController
public class SecurityDiaryRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    private final UpdateDeleteDiaryService updateDeleteDiaryService;

    private final FindDiaryService findDiaryService;

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService, UpdateDeleteDiaryService updateDeleteDiaryService, FindDiaryService findDiaryService) {
        this.saveDiaryService = saveDiaryService;
        this.updateDeleteDiaryService = updateDeleteDiaryService;
        this.findDiaryService = findDiaryService;
    }

    /**
     * @param principalDetails 로그인한 사용자 정보
     * @param dto              일지 정보 (일지, 식단, 음식 모두 포함)
     * @return 일지 정상 작성 여부
     */
    @PostMapping("/api/diary/user/diabetes-diary")
    public ApiResult<SecurityDiaryPostResponseDTO> postDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody @Valid SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary with authenticated user");

        Long writerId = principalDetails.getWriter().getId();

        /*JSON 직렬화가 LocalDateTime 에는 적용이 안되서 작성한 코드. */
        String date = dto.getYear() + "-" + dto.getMonth() + "-" + dto.getDay() + " " + dto.getHour() + ":" + dto.getMinute() + ":" + dto.getSecond();
        LocalDateTime writtenTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        /* 혈당 일지 저장 */
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, writerId), dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);

        Long diaryId = diary.getId();

        /* 아침 점심 저녁 식단 저장 */
        Diet breakFast = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.BreakFast, dto.getBreakFastSugar());
        Diet lunch = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.Lunch, dto.getLunchSugar());
        Diet dinner = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.Dinner, dto.getDinnerSugar());

        /* 아침 음식 저장 */
        dto.getBreakFastFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, breakFast.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));
        /* 점심 음식 저장 */
        dto.getLunchFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, lunch.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));
        /* 식단 음식 저장 */
        dto.getDinnerFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, dinner.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));
        return ApiResult.OK(new SecurityDiaryPostResponseDTO(diaryId));
    }

    /**
     * @param principalDetails 로그인 사용자 정보
     * @param dto              일지 수정 정보 (일지, 식단, 음식 포함)
     * @return 정상 수정 여부
     */
    @PutMapping("/api/diary/user/diabetes-diary")
    public ApiResult<SecurityDiaryUpdateResponseDTO> updateDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody SecurityDiaryUpdateDTO dto) {
        logger.info("update diabetes diary from browser");
        logger.info(dto.toString());

        EntityId<Writer, Long> writerLongEntityId = EntityId.of(Writer.class, principalDetails.getWriter().getId());
        EntityId<DiabetesDiary, Long> diabetesDiaryLongEntityId = EntityId.of(DiabetesDiary.class, dto.getDiaryId());
        EntityId<Diet, Long> breakFastEntityId = EntityId.of(Diet.class, dto.getBreakFastId());
        EntityId<Diet, Long> lunchEntityId = EntityId.of(Diet.class, dto.getLunchId());
        EntityId<Diet, Long> dinnerEntityId = EntityId.of(Diet.class, dto.getDinnerId());

        /* 혈당 일지 변경 감지 되었으면 수정. */
        if (dto.isDiaryDirty()) {
            updateDeleteDiaryService.updateDiary(writerLongEntityId, diabetesDiaryLongEntityId, dto.getFastingPlasmaGlucose(), dto.getRemark());
        }

        /* 아침 식단 변경 감지 되었으면 수정 */
        if (dto.isBreakFastDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, breakFastEntityId, EatTime.BreakFast, dto.getBreakFastSugar());
        }

        /* 점심 식단 변경 감지 되었으면 수정 */
        if (dto.isLunchDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, lunchEntityId, EatTime.Lunch, dto.getLunchSugar());
        }

        /* 저녁 식단 변경 감지 되었으면 수정 */
        if (dto.isDinnerDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, dinnerEntityId, EatTime.Dinner, dto.getDinnerSugar());
        }

        /* 기존 음식 엔티티 삭제 ( in (id) 벌크 삭제) */
        List<SecurityFoodForUpdateDTO> allOldFoods = new ArrayList<>();
        allOldFoods.addAll(dto.getOldBreakFastFoods());
        allOldFoods.addAll(dto.getOldLunchFoods());
        allOldFoods.addAll(dto.getOldDinnerFoods());

        if (allOldFoods.size() > 0) {
            List<EntityId<Food, Long>> foodEntityIds = allOldFoods.stream().map(elem -> EntityId.of(Food.class, elem.getId())).collect(Collectors.toList());
            updateDeleteDiaryService.bulkDeleteFoods(foodEntityIds);
        }

        /* 음식 엔티티 새로이 생성 */
        dto.getNewBreakFastFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                breakFastEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getNewLunchFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                lunchEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getNewDinnerFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                dinnerEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));
        return ApiResult.OK(new SecurityDiaryUpdateResponseDTO(dinnerEntityId.getId()));
    }

    /**
     * 일지 및 일지와 관련된 엔티티 "전부" 삭제
     *
     * @param principalDetails 로그인 사용자 정보
     * @param diaryId          삭제할 일지 id
     */
    @DeleteMapping("/api/diary/user/diabetes-diary/{diaryId}")
    public void bulkDeleteDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId) {
        logger.info("bulk delete Diabetes Diary from browser");
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, diaryId));
    }

    /**
     * @param principalDetails 사용자 유저 정보
     * @param year             년도
     * @param month            월
     * @param startDay         시작 날짜
     * @param endDay           끝 날짜
     * @return 해당 기간에 존재하는 일지들 반환 (연관된 엔티티는 포함하지 않는다.)
     */
    @GetMapping("/api/diary/user/diabetes-diary/list")
    public ApiResult<List<DiaryListBetweenTimeDTO>> getDiariesBetweenStartAndEnd(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                 @RequestParam(value = "year") int year, @RequestParam(value = "month") int month,
                                                                                 @RequestParam(value = "startDay") int startDay,
                                                                                 @RequestParam(value = "endDay") int endDay) {
        logger.info("get diaries between time");

        /* 문자열 파라미터 -> LocalDateTime 변환 */
        LocalDateTime startDate = LocalDateTime.of(year, month, startDay, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, endDay, 0, 0, 0);
        logger.info(startDate + " ~ " + endDate);

        /* fetch join 안했음. 달력에서는 id 값과 작성 날짜만 필요하기 때문. */
        List<DiabetesDiary> diaries =
                findDiaryService.getDiariesBetweenLocalDateTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()),
                        startDate, endDate);
        /* (일지 id, 작성 시간)의 형태의 dto 생성 */
        List<DiaryListBetweenTimeDTO> dtoList = diaries
                .stream().map(DiaryListBetweenTimeDTO::new)
                .collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }
}
