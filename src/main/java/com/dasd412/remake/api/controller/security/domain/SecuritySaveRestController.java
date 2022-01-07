package com.dasd412.remake.api.controller.security.domain;

import com.dasd412.remake.api.service.domain.SaveDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecuritySaveRestController {
    //시큐리티에서는 인증이 이미 되있기 때문에 기존 url 은
    // '/api/diary/user/'
    private final SaveDiaryService saveDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SecuritySaveRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

}
