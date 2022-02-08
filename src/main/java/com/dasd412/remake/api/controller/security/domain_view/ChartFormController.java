/*
 * @(#)ChartFormController.java        1.0.6 2022/2/8
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
 * @version 1.0.6 2022년 2월 8일
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
        return "chart/chartMenu";
    }

    /**
     * @return 기간 내 공복 혈당 화면
     */
    @GetMapping("/chart-menu/chart/fasting-plasma-glucose")
    public String showChartFastingPlasmaGlucose() {
        logger.info("chart fasting-plasma-glucose resolve");
        return "chart/fastingPlasmaGlucose";
    }

    /**
     * @return 기간 내 식사 혈당 화면
     */
    @GetMapping("/chart-menu/chart/blood-sugar")
    public String showChartBloodSugarBetweenTime() {
        logger.info("chart blood sugar  resolve");
        return "chart/bloodSugar";
    }

    /**
     * @return 평균 혈당 화면
     */
    @GetMapping("/chart-menu/chart/average")
    public String showChartAverage() {
        logger.info("chart average resolve");
        return "chart/average";
    }

    /**
     * @return 음식 화면
     */
    @GetMapping("/chart-menu/chart/food-board")
    public String showChartFoodBoard() {
        logger.info("chart food resolve");
        return "chart/foodBoard";
    }
}
