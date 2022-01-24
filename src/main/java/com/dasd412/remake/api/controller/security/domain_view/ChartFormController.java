/*
 * @(#)ChartFormController.java        1.0.2 2022/1/23
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 차트와 관련된 화면을 담당하는 컨트롤러
 *
 * @author 양영준
 * @version 1.0.2 2022년 1월 23일
 */
@Controller
public class ChartFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @return 차트 메뉴 화면
     */
    @GetMapping("/chart-menu")
    public String showChartMenu() {
        logger.info("chart menu view resolve");
        return "/chart/chartMenu";
    }

    /**
     * @return 혈당 추이 화면
     */
    @GetMapping("/chart-menu/chart/transition")
    public String showChartTransition() {
        logger.info("chart transition resolve");
        return "/chart/transition";
    }

    /**
     * @return 기간 내 공복 혈당 화면
     */
    @GetMapping("/chart-menu/chart/fasting-plasma-glucose")
    public String showChartFastingPlasmaGlucose() {
        logger.info("chart fasting-plasma-glucose resolve");
        return "/chart/fastingPlasmaGlucose";
    }

    /**
     * @return 기간 내 식사 혈당 화면
     */
    @GetMapping("/chart-menu/chart/blood-sugar/between-time")
    public String showChartBloodSugarBetweenTime() {
        logger.info("chart blood sugar between time resolve");
        return "/chart/bloodSugarBetweenTime";
    }

    /**
     * @return 식사 기준 식사 혈당 화면
     */
    @GetMapping("/chart-menu/chart/blood-sugar/eat-time")
    public String showChartBloodSugarEatTime() {
        logger.info("chart blood sugar eat time resolve");
        return "/chart/bloodSugarEatTime";
    }

    /**
     * @return 평균 혈당 화면
     */
    @GetMapping("/chart-menu/chart/average")
    public String showChartAverage() {
        logger.info("chart average resolve");
        return "/chart/average";
    }

    /**
     * @return 음식 화면
     */
    @GetMapping("/chart-menu/chart/food")
    public String showChartFood() {
        logger.info("chart food resolve");
        return "/chart/food";
    }
}
