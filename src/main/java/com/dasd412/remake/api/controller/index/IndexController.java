package com.dasd412.remake.api.controller.index;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping({"", "/"})
    public String viewResolveIndexPage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        if (principalDetails != null && principalDetails.getWriter() != null) {
            Writer user = principalDetails.getWriter();
            model.addAttribute("userPK", user.getId());
            model.addAttribute("userNickName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "index";
    }

}
