/*
 * @(#)GlobalControllerAdvice.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * 모든 컨트롤러에서 쓰이는 작업을 일일이 컨트롤러에 작성하면 중복코드가 발생한다.
 * 이를 방지하기 위한 ControllerAdvice
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 모든 뷰 컨트롤러와 에러 페이지에서 공통적으로 세션정보를 담아 주는 메서드.
     *
     * @param model            모델 객체
     * @param principalDetails 시큐리티에 담긴 사용자 인증 정보 객체
     */
    @ModelAttribute
    public void modelAddSessionInfo(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null && principalDetails.getWriter() != null) {
            Writer user = principalDetails.getWriter();
            model.addAttribute("userEmail", user.getEmail());
        }
    }
}
