package com.dasd412.remake.api.controller.index;

import com.dasd412.remake.api.config.security.dto.SessionWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    private final HttpSession httpSession;

    public IndexController(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @GetMapping({"", "/"})
    public String viewResolveIndexPage(Model model) {
        SessionWriter user = (SessionWriter) httpSession.getAttribute("user");

        if (user != null) {
            model.addAttribute("userPK", user.getWriterId());
            model.addAttribute("userNickName", user.getName());
            model.addAttribute("userEmail", user.getEmail());
        }
        return "index";
    }

}
