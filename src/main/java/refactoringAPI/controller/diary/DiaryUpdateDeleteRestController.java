package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import refactoringAPI.service.UpdateDeleteDiaryService;

@RestController
public class DiaryUpdateDeleteRestController {

    private final UpdateDeleteDiaryService updateDeleteDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryUpdateDeleteRestController(UpdateDeleteDiaryService updateDeleteDiaryService) {
        this.updateDeleteDiaryService = updateDeleteDiaryService;
    }
}
