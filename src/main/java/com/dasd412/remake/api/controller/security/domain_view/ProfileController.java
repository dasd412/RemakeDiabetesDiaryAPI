/*
 * @(#)ProfileController.java        1.1.1 2022/2/26
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
 * 프로필 메뉴 클릭 시  사용되는 컨트롤러
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 26일
 */
@Controller
public class ProfileController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/profile")
    public String profileViewResolve(){
        logger.info("profile view resolve");
        return "/profile/profile";
    }
}
