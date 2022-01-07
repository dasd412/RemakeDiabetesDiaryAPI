package com.dasd412.remake.api.controller;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    //모든 뷰 컨트롤러와 에러 페이지에서 공통적으로 세션정보를 ModelAttribute 하는 로직. 중복 코드를 줄일 수 있다.
    @ModelAttribute
    public void modelAddSessionInfo(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null && principalDetails.getWriter() != null) {
            Writer user = principalDetails.getWriter();
            model.addAttribute("userEmail", user.getEmail());
        }
    }
}
