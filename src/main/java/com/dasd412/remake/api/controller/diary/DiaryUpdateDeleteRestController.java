package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.service.UpdateDeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiaryUpdateDeleteRestController {

    private final UpdateDeleteDiaryService updateDeleteDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryUpdateDeleteRestController(UpdateDeleteDiaryService updateDeleteDiaryService) {
        this.updateDeleteDiaryService = updateDeleteDiaryService;
    }
}
