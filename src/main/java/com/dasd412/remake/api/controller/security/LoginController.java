package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.config.auth.PrincipalDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/loginForm")
    public String loginForm() {
        logger.info("loginForm view resolve");
        return "/login/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        logger.info("joinForm view resolve");
        return "/login/joinForm";
    }

    //todo 테스트 코드이므로 지울 예정
    @GetMapping("api/diary/diabetes-diary")
    public String diary(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("diary view resolve");
        return "/diary/diary";
    }

}
