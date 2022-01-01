package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.controller.exception.DuplicateEmailException;
import com.dasd412.remake.api.controller.exception.DuplicateUserNameException;
import com.dasd412.remake.api.controller.security.writer.UserJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.service.security.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginRestController {

    private final WriterService writerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginRestController(WriterService writerService) {
        this.writerService = writerService;
    }

    @PostMapping("/join")
    public String join(@RequestBody UserJoinRequestDTO dto) {
        logger.info("writer join");

        try{
            writerService.saveWriterWithSecurity(dto.getName(), dto.getEmail(), dto.getPassword(), Role.User);
        }catch (DuplicateUserNameException nameException){

        }catch (DuplicateEmailException emailException){

        }

        return "redirect:/loginForm";
    }
}
