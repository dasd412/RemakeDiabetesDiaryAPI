/*
 * @(#)SecurityChartRestController.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.*;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import com.dasd412.remake.api.util.DateStringConverter;
import com.querydsl.core.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 로그인한 사용자들이 자신의 혈당 "정보"를 차트 형태로 조회할 수 있게 하는 RestController
 */
@RestController
public class SecurityChartRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FindDiaryService findDiaryService;

    public SecurityChartRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    /**
     * @return 전체 기간 내 공복 혈당
     */
    @GetMapping("/chart-menu/fasting-plasma-glucose/all")
    public ApiResult<List<FindAllFpgDTO>> findAllFpg(@AuthenticationPrincipal PrincipalDetails principalDetails) {

        logger.info("find all fasting-plasma-glucose ..");
        List<DiabetesDiary> diaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        List<FindAllFpgDTO> dtoList = diaryList.stream().map(FindAllFpgDTO::new).sorted(Comparator.comparing(FindAllFpgDTO::getTimeByTimeStamp)).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @return 해당 기간 내 공복 혈당
     */
    @GetMapping("/chart-menu/fasting-plasma-glucose/between")
    public ApiResult<List<FindFpgBetweenDTO>> findFpgBetweenTime(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> startYearMonthDayEndYearMonthDay) {

        LocalDateTime startDate = DateStringConverter.convertMapParamsToStartDate(startYearMonthDayEndYearMonthDay);
        LocalDateTime endDate = DateStringConverter.convertMapParamsToEndDate(startYearMonthDayEndYearMonthDay);

        logger.info("find fpg between" + startDate + " and " + endDate);

        List<DiabetesDiary> diaryList = findDiaryService.getDiariesBetweenLocalDateTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);
        List<FindFpgBetweenDTO> dtoList = diaryList.stream().map(FindFpgBetweenDTO::new).sorted(Comparator.comparing(FindFpgBetweenDTO::getTimeByTimeStamp)).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }
    
    @GetMapping("/chart-menu/blood-sugar/all")
    public ApiResult<List<FindAllBloodSugarDTO>> findAllBloodSugar(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find all blood sugar");
        List<DiabetesDiary> diaries = findDiaryService.getDiabetesDiariesOfWriterWithRelation(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        List<FindAllBloodSugarDTO> dtoList = new ArrayList<>();
        for (DiabetesDiary diary : diaries) {
            for (Diet diet : diary.getDietList()) {
                dtoList.add(new FindAllBloodSugarDTO(diary, diet));
            }
        }
        
        dtoList.sort(Comparator.comparing(FindAllBloodSugarDTO::getDateTime));

        return ApiResult.OK(dtoList);
    }

    /**
     * @return 해당 기간 내 혈당 일지 및 식단 정보
     */
    @GetMapping("/chart-menu/blood-sugar/between")
    public ApiResult<List<FindBloodSugarBetweenDTO>> findBloodSugarBetween(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> startYearMonthDayEndYearMonthDay) {
        LocalDateTime startDate = DateStringConverter.convertMapParamsToStartDate(startYearMonthDayEndYearMonthDay);
        LocalDateTime endDate = DateStringConverter.convertMapParamsToEndDate(startYearMonthDayEndYearMonthDay);

        logger.info("find blood sugar between" + startDate + " and " + endDate);

        List<DiabetesDiary> diaries = findDiaryService.getDiariesWithRelationBetweenTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);

        List<FindBloodSugarBetweenDTO> dtoList = new ArrayList<>();

        for (DiabetesDiary diary : diaries) {
            for (Diet diet : diary.getDietList()) {
                dtoList.add(new FindBloodSugarBetweenDTO(diary, diet));
            }
        }
        
        dtoList.sort(Comparator.comparing(FindBloodSugarBetweenDTO::getDateTime));

        return ApiResult.OK(dtoList);
    }

    /**
     * @return 사용자의 평균 공복 혈당 정보 + 사용자의 식사 시간 별 평균 혈당 정보 + 사용자의 전체 식사 평균 혈당 정보
     */
    @GetMapping("/chart-menu/average/all")
    public ApiResult<FindAverageAllDTO> findAverageAll(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find average all");
        Double averageFpg = findDiaryService.getAverageFpg(EntityId.of(Writer.class, principalDetails.getWriter().getId()));
        List<Tuple> averageBloodSugarTuples = findDiaryService.getAverageBloodSugarGroupByEatTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()));
        Double averageBloodSugar = findDiaryService.getAverageBloodSugarOfDiet(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        return ApiResult.OK(FindAverageAllDTO.builder().averageFpg(averageFpg).tupleList(averageBloodSugarTuples).averageBloodSugar(averageBloodSugar).build());
    }

    /**
     * @return 해당 기간 내 혈당 일지 및 식단 정보 (평균)
     */
    @GetMapping("/chart-menu/average/between")
    public ApiResult<FindAverageBetweenDTO> findAverageBetween(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> startYearMonthDayEndYearMonthDay) {
        LocalDateTime startDate = DateStringConverter.convertMapParamsToStartDate(startYearMonthDayEndYearMonthDay);
        LocalDateTime endDate = DateStringConverter.convertMapParamsToEndDate(startYearMonthDayEndYearMonthDay);

        logger.info("find average blood sugar between" + startDate + " and " + endDate);

        Double averageFpgBetween = findDiaryService.getAverageFpgBetween(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);
        List<Tuple> averageBloodSugarTuplesBetween = findDiaryService.getAverageBloodSugarGroupByEatTimeBetweenTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);
        Double averageBloodSugarBetween = findDiaryService.getAverageBloodSugarOfDietBetweenTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);

        return ApiResult.OK(FindAverageBetweenDTO.builder().averageFpgBetween(averageFpgBetween).tupleListBetween(averageBloodSugarTuplesBetween).averageBloodSugarBetween(averageBloodSugarBetween).build());
    }
}
