/*
 * @(#)FindInfoRestController.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.info;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.exception.OAuthFindUsernameException;
import com.dasd412.remake.api.controller.exception.PasswordNotMatchException;
import com.dasd412.remake.api.controller.security.info.dto.TempPasswordDTO;
import com.dasd412.remake.api.service.security.EmailService;
import com.dasd412.remake.api.service.security.FindInfoService;
import com.dasd412.remake.api.service.security.WriterService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 아이디 찾기 등 유저 정보 검색 및 이메일 제공
 */
@RestController
public class FindInfoRestController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterService writerService;
    private final FindInfoService findInfoService;
    private final EmailService emailService;


    public FindInfoRestController(WriterService writerService, FindInfoService findInfoService, EmailService emailService) {
        this.writerService = writerService;
        this.findInfoService = findInfoService;
        this.emailService = emailService;
    }

    /**
     * @return 이메일에 해당하는, 찾고자 하는 유저 id
     */
    @GetMapping("/user-info/user-name")
    public ApiResult<?> findUserName(@RequestParam(value = "email") String email) {

        logger.info("find user name by email");
        String userName;

        try {
            userName = findInfoService.getUserNameByEmail(email);
        } catch (UsernameNotFoundException | OAuthFindUsernameException exception) {
            return ApiResult.ERROR(exception, HttpStatus.BAD_REQUEST);
        } catch (IllegalArgumentException exception) {
            return ApiResult.ERROR(new IllegalArgumentException("올바르지 않은 이메일 형식입니다."), HttpStatus.BAD_REQUEST);
        }

        emailService.sendEmailAboutId(email, userName);

        return ApiResult.OK("sent email");
    }

    /**
     * @return 새로운 임시 비밀 번호
     */
    @GetMapping("/user-info/password")
    public ApiResult<?> findPassword(@RequestParam(value = "email") String email, @RequestParam(value = "userName") String userName) {

        logger.info("find user password");

        boolean exist;

        try {
            exist = findInfoService.existPassword(email, userName);
        } catch (IllegalArgumentException e) {
            return ApiResult.ERROR(new IllegalArgumentException("올바르지 않은 이메일 형식입니다."), HttpStatus.BAD_REQUEST);
        }

        if (exist) {
            String tempPassword = writerService.issueNewPassword();
            /* 새 임시 비밀 번호 영속화 */
            writerService.updateTempPassword(email, userName, tempPassword);

            emailService.sendEmailAboutTempPassword(email, tempPassword);

            return ApiResult.OK(new TempPasswordDTO(tempPassword));
        } else {
            return ApiResult.ERROR(new PasswordNotMatchException("입력하신 id, email에 해당하는 비밀번호가 존재하지 않습니다!!"), HttpStatus.NOT_FOUND);
        }
    }
}
