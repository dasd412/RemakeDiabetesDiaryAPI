/*
 * @(#)ChartFormController.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_view;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
public class ChartFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FindDiaryService findDiaryService;

    public ChartFormController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    @GetMapping("/chart-menu")
    public String showChartMenu() {
        logger.info("chart menu view resolve");
        return "chart/chartMenu";
    }

    @GetMapping("/chart-menu/chart/fasting-plasma-glucose")
    public String showChartFastingPlasmaGlucose() {
        logger.info("chart fasting-plasma-glucose resolve");
        return "chart/fastingPlasmaGlucose";
    }

    @GetMapping("/chart-menu/chart/blood-sugar")
    public String showChartBloodSugarBetweenTime() {
        logger.info("chart blood sugar  resolve");
        return "chart/bloodSugar";
    }

    @GetMapping("/chart-menu/chart/average")
    public String showChartAverage() {
        logger.info("chart average resolve");
        return "chart/average";
    }

    /**
     * @return 음식 게시판 화면
     */
    @GetMapping("/chart-menu/chart/food-board/list")
    public String showChartFoodBoard(@AuthenticationPrincipal PrincipalDetails principalDetails, @ModelAttribute("foodPageVO") FoodPageVO vo, Model model) {
        logger.info("show chart food board : " + vo.toString());

        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, principalDetails.getWriter().getId()), vo);

        logger.info("dto : " + dtoPage);
        model.addAttribute("dtoPage", new FoodPageMaker<>(dtoPage));

        return "chart/foodBoard";
    }
}
