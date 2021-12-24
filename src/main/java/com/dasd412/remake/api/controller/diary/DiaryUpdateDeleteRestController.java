package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryDeleteResponseDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryUpdateResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.UpdateDeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class DiaryUpdateDeleteRestController {

    private final UpdateDeleteDiaryService updateDeleteDiaryService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DiaryUpdateDeleteRestController(UpdateDeleteDiaryService updateDeleteDiaryService) {
        this.updateDeleteDiaryService = updateDeleteDiaryService;
    }

    /*
    Http PUT 메서드는 요청 페이로드를 사용해 새로운 데이터를 생성하거나, 대상 리소스를 나타내는 데이터를 "대체"한다.
    PUT 과 POST 의 차이점은 "멱등성"이다.
    동일한 요청을 한 번 보내는 것과 여러 번 연속으로 보내는 것의 응답이 똑같으면, "멱등성"을 만족한다.
    PUT 은 멱등성을 가지지만, POST 는 멱등성이 없다.
     */

    /*
    일지 수정 및 삭제 메서드
     */
    @PutMapping("api/diary/diabetes_diary")
    public ApiResult<DiaryUpdateResponseDTO> updateDiabetesDiary(@RequestBody DiaryUpdateRequestDTO dto) {
        logger.info("update diabetes diary");

        return ApiResult.OK(new DiaryUpdateResponseDTO(updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, dto.getWriterId()), EntityId.of(DiabetesDiary.class, dto.getDiaryId()), dto.getFastingPlasmaGlucose(), dto.getRemark())));
    }

    @DeleteMapping("api/diary/owner/{writerId}/diabetes_diary/{diaryId}")
    public ApiResult<DiaryDeleteResponseDTO> deleteDiabetesDiary(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("delete Diabetes Diary");
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId));

        return ApiResult.OK(new DiaryDeleteResponseDTO(writerId, diaryId));
    }


}
