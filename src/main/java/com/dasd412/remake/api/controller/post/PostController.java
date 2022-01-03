package com.dasd412.remake.api.controller.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/post")
    public String loginForm() {
        logger.info("post view resolve");
        return "/post/post";
    }
}
