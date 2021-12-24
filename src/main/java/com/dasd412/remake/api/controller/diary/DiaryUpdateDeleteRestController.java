package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryDeleteResponseDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryUpdateResponseDTO;
import com.dasd412.remake.api.controller.diary.diet.DietDeleteResponseDTO;
import com.dasd412.remake.api.controller.diary.diet.DietUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietUpdateResponseDTO;
import com.dasd412.remake.api.controller.diary.food.FoodDeleteResponseDTO;
import com.dasd412.remake.api.controller.diary.food.FoodUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodUpdateResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.food.Food;
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

    /*
    식단 수정 및 삭제 메서드
     */
    @PutMapping("api/diary/diet")
    public ApiResult<DietUpdateResponseDTO> updateDiet(@RequestBody DietUpdateRequestDTO dto) {
        logger.info("update diet");

        return ApiResult.OK(new DietUpdateResponseDTO(updateDeleteDiaryService.updateDiet(EntityId.of(Writer.class, dto.getWriterId()), EntityId.of(DiabetesDiary.class, dto.getDiaryId()), EntityId.of(Diet.class, dto.getDietId()), dto.getEatTime(), dto.getBloodSugar())));
    }

    @DeleteMapping("api/diary/owner/{writerId}/diabetes_diary/{diaryId}/diet/{dietId}")
    public ApiResult<DietDeleteResponseDTO> deleteDiet(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId, @PathVariable("dietId") Long dietId) {
        logger.info("delete diet");

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EntityId.of(Diet.class, dietId));

        return ApiResult.OK(new DietDeleteResponseDTO(writerId, diaryId, dietId));
    }

    /*
    음식 수정 및 삭제 메서드
     */
    @PutMapping("api/diary/food")
    public ApiResult<FoodUpdateResponseDTO> updateFood(@RequestBody FoodUpdateRequestDTO dto) {
        logger.info("update food");

        return ApiResult.OK(new FoodUpdateResponseDTO(updateDeleteDiaryService.updateFood(EntityId.of(Writer.class, dto.getWriterId()), EntityId.of(Diet.class, dto.getDietId()), EntityId.of(Food.class, dto.getFoodId()), dto.getFoodName())));
    }

    @DeleteMapping("api/diary/owner/{writerId}/diabetes_diary/{diaryId}/diet/{dietId}/food/{foodId}")
    public ApiResult<FoodDeleteResponseDTO> deleteFood(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId, @PathVariable("dietId") Long dietId, @PathVariable("foodId") Long foodId) {
        logger.info("delete food");

        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EntityId.of(Diet.class, dietId), EntityId.of(Food.class, foodId));

        return ApiResult.OK(new FoodDeleteResponseDTO(writerId, diaryId, dietId, foodId));
    }

}
