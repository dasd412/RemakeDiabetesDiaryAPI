package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.exception.DuplicateEmailException;
import com.dasd412.remake.api.controller.exception.DuplicateUserNameException;
import com.dasd412.remake.api.controller.security.writer.UserJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.service.security.WriterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class LoginRestController {

    private final WriterService writerService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public LoginRestController(WriterService writerService) {
        this.writerService = writerService;
    }

    @PostMapping("/signup/user")
    public ApiResult signup(@RequestBody @Valid UserJoinRequestDTO dto) {
        logger.info("writer join");

        try {
            //사용자 정의 회원 가입 시에는 provider 관련 데이터가 필요없다.
            writerService.saveWriterWithSecurity(dto.getName(), dto.getEmail(), dto.getPassword(), Role.User,null,null);
        } catch (DuplicateUserNameException nameException) {
            return ApiResult.ERROR("duplicateName", HttpStatus.BAD_REQUEST);
        } catch (DuplicateEmailException emailException) {
            return ApiResult.ERROR("duplicateEmail", HttpStatus.BAD_REQUEST);
        }

        return ApiResult.OK("회원 가입 완료하였습니다.");
    }
}