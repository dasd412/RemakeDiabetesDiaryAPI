package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import refactoringAPI.service.SaveDiaryService;

@RestController
public class DiarySaveRestController {

    private final SaveDiaryService saveDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiarySaveRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }


}
