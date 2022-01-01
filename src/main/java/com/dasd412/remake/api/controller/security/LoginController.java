package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.controller.security.writer.UserJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.service.security.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {

    private final WriterService writerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginController(WriterService writerService) {
        this.writerService = writerService;
    }

    @GetMapping("/loginForm")
    public String loginForm() {
        logger.info("loginForm view resolve");
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        logger.info("joinForm view resolve");
        return "joinForm";
    }

    @GetMapping("api/diary/diabetes-diary")
    public String diary() {
        logger.info("diary view resolve");
        return "diary";
    }

    @PostMapping("/join")
    public String join(@RequestBody UserJoinRequestDTO dto) {
        logger.info("writer join");

        writerService.saveWriterWithSecurity(dto.getName(), dto.getEmail(), dto.getPassword(), Role.User);

        return "redirect:/loginForm";
    }

}
