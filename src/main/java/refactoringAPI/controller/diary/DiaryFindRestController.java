package refactoringAPI.controller.diary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import refactoringAPI.controller.ApiResult;
import refactoringAPI.controller.diary.diabetesdiary.DiabetesDiaryFindResponseDTO;
import refactoringAPI.controller.diary.diabetesdiary.DiaryListBetweenTimeRequestDTO;
import refactoringAPI.controller.diary.diabetesdiary.DiaryListFindResponseDTO;
import refactoringAPI.controller.diary.writer.WriterFindResponseDTO;
import refactoringAPI.domain.diary.EntityId;
import refactoringAPI.domain.diary.diabetesDiary.DiabetesDiary;
import refactoringAPI.domain.diary.writer.Writer;
import refactoringAPI.service.FindDiaryService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class DiaryFindRestController {
    /*
    컨트롤러 계층에는 엔티티가 사용되선 안되며 DTO 로 감싸줘야 한다.
     */

    /*
    Missing URI template variable for method parameter of type string 관련 에러는
    @GetMapping() 내의 id 값과 파라미터로 들어가는 id가 이름이 서로 다를 경우에 발생한다.
    해결 방법 중 하나는 @PathVariable()에 변수를 할당하는 것이다.

    ex)
    @GetMapping("/{userId}")
    ->@PathVariable("userId") Long id
     */
    private final FindDiaryService findDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryFindRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    @GetMapping("api/diary/owner/diabetes_diary/{id}")
    public ApiResult<WriterFindResponseDTO> findOwnerOfDiary(@PathVariable("id") Long diaryId) {
        logger.info("find owner of the diary");

        return ApiResult.OK(new WriterFindResponseDTO(findDiaryService.getWriterOfDiary(EntityId.of(DiabetesDiary.class, diaryId))));
    }

    @GetMapping("api/diary/owner/{id}/diabetes_diary/list")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiabetesDiariesOfOwner(@PathVariable("id") Long writerId) {
        logger.info("find diabetes diaries of the owner");

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, writerId));
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes_diary/{diaryId}")
    public ApiResult<DiabetesDiaryFindResponseDTO> findOneDiabetesDiaryOfOwner(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find only one diabetes diary of the owner");

        return ApiResult.OK(new DiabetesDiaryFindResponseDTO(findDiaryService.getDiabetesDiaryOfWriter(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId))));
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes_diary/start_date/{startDate}/end_date/{endDate}")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiariesBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Diaries Between Time ->" + startDate + " : " + endDate);

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiariesBetweenTime(EntityId.of(Writer.class, writerId), startDate, endDate);
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }
}
