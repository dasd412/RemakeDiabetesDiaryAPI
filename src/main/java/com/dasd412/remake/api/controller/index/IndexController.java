/*
 * @(#)IndexController.java        1.0.8 2022/2/16
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 대문 화면 resolve 용 컨트롤러.
 *
 * @author 양영준
 * @version 1.0.8 2022년 2월 16일
 */
@Controller
public class IndexController {

    @GetMapping({"", "/"})
    public String viewResolveIndexPage() {
        return "index";
    }

}
