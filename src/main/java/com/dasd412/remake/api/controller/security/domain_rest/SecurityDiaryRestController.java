/*
 * @(#)SecurityDiaryRestController.java        1.1.7 2022/3/23
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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 로그인한 사용자들이 일지를 "사용" (작성, 수정, 삭제 등...)할 때의 일을 처리하는 RestController
 *
 * @author 양영준
 * @version 1.1.7 2022년 3월 23일
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

        Long diaryId = saveDiaryService.postDiaryWithEntities(principalDetails, dto);

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

        Long diaryId= updateDeleteDiaryService.updateDiaryWithEntities(principalDetails,dto);

        return ApiResult.OK(new SecurityDiaryUpdateResponseDTO(diaryId));
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
