/*
 * @(#)FindInfoRestController.java        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.info;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.exception.OAuthFindUsernameException;
import com.dasd412.remake.api.controller.security.info.dto.FindIdRequestDTO;
import com.dasd412.remake.api.service.security.EmailService;
import com.dasd412.remake.api.service.security.FindInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    private final EmailService emailService;

    public FindInfoRestController(FindInfoService findInfoService, EmailService emailService) {
        this.findInfoService = findInfoService;
        this.emailService = emailService;
    }

    @GetMapping("/user-info/user-name")
    public ApiResult<?> findUserName(@RequestBody @Valid FindIdRequestDTO dto) {
        logger.info("find user name");
        String userName;

        try {
            userName = findInfoService.getUserNameByEmail(dto.getEmail());
        } catch (UsernameNotFoundException | OAuthFindUsernameException exception) {
            return ApiResult.ERROR(exception, HttpStatus.valueOf(404));
        }

        emailService.sendEmailAboutId(dto.getEmail(), userName);

        return ApiResult.OK("sent email");
    }

    @GetMapping("/user-info/password")
    public void findPassword() {
        logger.info("find user password");

    }

}
