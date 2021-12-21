package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import refactoringAPI.controller.ApiResult;
import refactoringAPI.controller.diary.writer.WriterFindResponseDTO;
import refactoringAPI.domain.diary.EntityId;
import refactoringAPI.domain.diary.diabetesDiary.DiabetesDiary;
import refactoringAPI.domain.diary.writer.Writer;
import refactoringAPI.service.FindDiaryService;

@RestController
public class DiaryFindRestController {
    /*
    컨트롤러 계층에는 엔티티가 사용되선 안되며 DTO 로 감싸줘야 한다.
     */
    private final FindDiaryService findDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryFindRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    @GetMapping("api/diary/owner/diabetesDiary/{id}")
    public ApiResult<WriterFindResponseDTO> findOwnerOfDiary(@PathVariable Long id) {
        logger.info("find owner of the diary");
        return ApiResult.OK(new WriterFindResponseDTO(findDiaryService.getWriterOfDiary(EntityId.of(DiabetesDiary.class, id))));
    }


}
