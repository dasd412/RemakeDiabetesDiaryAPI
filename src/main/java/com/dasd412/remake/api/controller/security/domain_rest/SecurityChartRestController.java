/*
 * @(#)SecurityChartRestController.java        1.0.4 2022/2/3
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
 * 로그인한 사용자들이 자신의 혈당 "정보"를 조회할 수 있게 하는 RestController
 *
 * @author 양영준
 * @version 1.0.4 2022년 2월 3일
 */
@RestController
public class SecurityChartRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FindDiaryService findDiaryService;

    public SecurityChartRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    /**
     * 사용자의 전체 기간 내 공복 혈당 조회.
     *
     * @param principalDetails 사용장 인증 정보
     * @return 전체 기간 내 공복 혈당
     */
    @GetMapping("/chart-menu/fasting-plasma-glucose/all")
    public ApiResult<List<FindAllFpgDTO>> findAllFpg(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find all fasting-plasma-glucose ..");
        List<DiabetesDiary> diaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        List<FindAllFpgDTO> dtoList = diaryList.stream().map(FindAllFpgDTO::new).sorted(Comparator.comparing(FindAllFpgDTO::getTimeByTimeStamp)).collect(Collectors.toList());

        //시간 순 정렬

        return ApiResult.OK(dtoList);
    }

    /**
     * 사용자의 해당 기간 내 공복 혈당 조회
     *
     * @param principalDetails 사용자 인증 정보
     * @param allParams        시작 연도, 시작 월, 시작 일, 끝 년도, 끝 월, 끝 일
     * @return 해당 기간 내 공복 혈당
     */
    @GetMapping("/chart-menu/fasting-plasma-glucose/between")
    public ApiResult<List<FindFpgBetweenDTO>> findFpgBetweenTime(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> allParams) {
        LocalDateTime startDate = convertStartDate(allParams);
        LocalDateTime endDate = convertEndDate(allParams);

        logger.info("find fpg between" + startDate + " and " + endDate);

        List<DiabetesDiary> diaryList = findDiaryService.getDiariesBetweenLocalDateTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);
        List<FindFpgBetweenDTO> dtoList = diaryList.stream().map(FindFpgBetweenDTO::new).sorted(Comparator.comparing(FindFpgBetweenDTO::getTimeByTimeStamp)).collect(Collectors.toList());

        //시간 순 정렬

        return ApiResult.OK(dtoList);
    }

    /**
     * @param principalDetails 사용자 인증 정보
     * @return 전체 기간 내 혈당 일지 및 식단 정보
     */
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

        //시간 순 정렬
        dtoList.sort(Comparator.comparing(FindAllBloodSugarDTO::getDateTime));

        return ApiResult.OK(dtoList);
    }

    /**
     * @param principalDetails 사용자 인증 정보
     * @param allParams        시작 연도, 시작 월, 시작 일, 끝 년도, 끝 월, 끝 일
     * @return 해당 기간 내 혈당 일지 및 식단 정보
     */
    @GetMapping("/chart-menu/blood-sugar/between")
    public ApiResult<List<FindBloodSugarBetweenDTO>> findBloodSugarBetween(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestParam Map<String, String> allParams) {
        LocalDateTime startDate = convertStartDate(allParams);
        LocalDateTime endDate = convertEndDate(allParams);

        logger.info("find blood sugar between" + startDate + " and " + endDate);

        List<DiabetesDiary> diaries = findDiaryService.getDiariesWithRelationBetweenTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()), startDate, endDate);

        List<FindBloodSugarBetweenDTO> dtoList = new ArrayList<>();

        for (DiabetesDiary diary : diaries) {
            for (Diet diet : diary.getDietList()) {
                dtoList.add(new FindBloodSugarBetweenDTO(diary, diet));
            }
        }

        //시간 순 정렬
        dtoList.sort(Comparator.comparing(FindBloodSugarBetweenDTO::getDateTime));

        return ApiResult.OK(dtoList);
    }

    /**
     * @param principalDetails 사용자 인증 정보
     * @return 사용자의 평균 공복 혈당 정보
     */
    @GetMapping("/chart-menu/average/fasting-plasma-glucose")
    public ApiResult<FindAverageFpgDTO> findAverageFpg(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find average fasting-plasma-glucose");
        Double averageFpg = findDiaryService.getAverageFpg(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        return ApiResult.OK(new FindAverageFpgDTO(averageFpg));
    }

    /**
     * @param principalDetails 사용자 인증 정보
     * @return 사용자의 평균 식사 혈당 정보 (식사 시간마다 따로 따로)
     */
    @GetMapping("/chart-menu/average/blood-sugar")
    public ApiResult<FindAverageBloodSugarDTO> findAverageBloodSugarGroupByEatTime(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find average blood-sugar");
        List<Tuple> averageBloodSugarTuples = findDiaryService.getAverageBloodSugarGroupByEatTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

        return ApiResult.OK(new FindAverageBloodSugarDTO(averageBloodSugarTuples));
    }

    /**
     * 중복 제거 리팩토링용 도우미 메서드
     *
     * @param allParams RequestParam 해시 맵
     * @return 시작 날짜
     */
    private LocalDateTime convertStartDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("startYear")),
                Integer.parseInt(allParams.get("startMonth")),
                Integer.parseInt(allParams.get("startDay")),
                0, 0);
    }

    /**
     * 중복 제거 리팩토링용 도우미 메서드
     *
     * @param allParams RequestParam 해시 맵
     * @return 끝 날짜
     */
    private LocalDateTime convertEndDate(Map<String, String> allParams) {
        return LocalDateTime.of(Integer.parseInt(allParams.get("endYear")),
                Integer.parseInt(allParams.get("endMonth")),
                Integer.parseInt(allParams.get("endDay")),
                0, 0);
    }

}
