package com.dasd412.remake.api.controller.security.domain;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SecurityDiaryRestController {
    //시큐리티에서는 인증이 이미 되있기 때문에 기존 url 은 관리자만 진입가능하게 바꿨다.
    private final SaveDiaryService saveDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

    @PostMapping("/api/diary/user/diabetes-diary")
    public void postDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody @Valid SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary with authenticated user");
        logger.info(dto.toString());
    }
}
