/*
 * @(#)SecurityChartRestController.java        1.0.2 2022/1/26
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 로그인한 사용자들이 자신의 혈당 "정보"를 조회할 수 있게 하는 RestController
 *
 * @author 양영준
 * @version 1.0.2 2022년 1월 26일
 */
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
     */
    @GetMapping("/chart-menu/fasting-plasma-glucose/all")
    public void findAllFpg(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("find all fasting-plasma-glucose ..");
        List<DiabetesDiary> diaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, principalDetails.getWriter().getId()));

    }
}
