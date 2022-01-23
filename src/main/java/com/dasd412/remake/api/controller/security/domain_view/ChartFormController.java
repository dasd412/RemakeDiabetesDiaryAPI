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

    @GetMapping("/chart-menu")
    public String showChartMenu() {
        logger.info("chart menu view resolve");
        return "/chart/chartMenu";
    }
}
