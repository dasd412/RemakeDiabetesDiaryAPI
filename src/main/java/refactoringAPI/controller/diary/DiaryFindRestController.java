package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;
import refactoringAPI.service.FindDiaryService;

@RestController
public class DiaryFindRestController {

    private final FindDiaryService findDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryFindRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }
}
