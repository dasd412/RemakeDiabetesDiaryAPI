/*
 * @(#)LoginController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 로그인폼 resolve 용 컨트롤러
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @param error     로그인 실패 여부
     * @param exception 로그인 실패 시 에러 내용
     * @param model     에러 내용을 담을 모델 객체
     * @return 로그인 폼 뷰
     */
    @GetMapping("/loginForm")
    public String loginForm(@RequestParam(value = "error", required = false) String error,
                            @RequestParam(value = "exception", required = false) String exception, Model model) {

        model.addAttribute("error", error);
        model.addAttribute("exception", exception);

        logger.info("loginForm view resolve");
        return "login/loginForm";
    }

    /**
     * @return 회원 가입 뷰
     */
    @GetMapping("/joinForm")
    public String joinForm() {
        logger.info("joinForm view resolve");
        return "login/joinForm";
    }

}
