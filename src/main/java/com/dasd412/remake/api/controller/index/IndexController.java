/*
 * @(#)IndexController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.index;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 대문 화면 resolve 용 컨트롤러.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Controller
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping({"", "/"})
    public String viewResolveIndexPage() {
        return "index";
    }

}
