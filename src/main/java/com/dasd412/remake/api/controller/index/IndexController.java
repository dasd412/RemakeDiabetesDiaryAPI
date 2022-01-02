package com.dasd412.remake.api.controller.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping({"", "/"})
    public String viewResolveIndexPage() {
        return "index";
    }

}
