/*
 * @(#)FindInfoRestController.java        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.service.security.FindInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 아이디 찾기 등 유저 정보 검색 및 이메일 제공
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 2일
 */
@RestController
public class FindInfoRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FindInfoService findInfoService;

    public FindInfoRestController(FindInfoService findInfoService) {
        this.findInfoService = findInfoService;
    }

    @GetMapping("/user-info/user-name")
    public void findUserName() {
        logger.info("find user name");

    }

    @GetMapping("/user-info/password")
    public void findPassword() {
        logger.info("find user password");

    }

}
