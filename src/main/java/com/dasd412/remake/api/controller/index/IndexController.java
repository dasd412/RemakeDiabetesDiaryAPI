package com.dasd412.remake.api.controller.index;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class IndexController {


    @GetMapping({"", "/"})
    public String viewResolveIndexPage(Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        Writer user
//        if (user != null) {
//            model.addAttribute("userPK", user.getWriterId());
//            model.addAttribute("userNickName", user.getName());
//            model.addAttribute("userEmail", user.getEmail());
//        }
        return "index";
    }

}
