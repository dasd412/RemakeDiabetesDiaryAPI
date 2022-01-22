/*
 * @(#)CalendarFormController.java        1.0.1 2022/1/22
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
 * 캘린더 메뉴 클릭 시  사용되는 컨트롤러
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Controller
public class CalendarFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/calendar")
    public String calendarViewResolve() {
        logger.info("calendar view resolve");
        return "calendar/calendar";
    }
}

