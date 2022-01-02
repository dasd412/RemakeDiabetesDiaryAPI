package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/loginForm")
    public String loginForm(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("loginForm view resolve");
        if (principalDetails != null && principalDetails.getWriter() != null) {
            Writer user = principalDetails.getWriter();
            model.addAttribute("userPK", user.getId());
            model.addAttribute("userNickName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "/login/loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        logger.info("joinForm view resolve");
        if (principalDetails != null && principalDetails.getWriter() != null) {
            Writer user = principalDetails.getWriter();
            model.addAttribute("userPK", user.getId());
            model.addAttribute("userNickName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "/login/joinForm";
    }

}
