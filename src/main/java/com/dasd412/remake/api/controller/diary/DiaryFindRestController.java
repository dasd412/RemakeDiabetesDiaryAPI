package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryFetchResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryFindResponseDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryListFindResponseDTO;
import com.dasd412.remake.api.controller.diary.diet.DietFindResponseDTO;
import com.dasd412.remake.api.controller.diary.diet.DietListFindResponseDTO;
import com.dasd412.remake.api.controller.diary.food.FoodFindResponseDTO;
import com.dasd412.remake.api.controller.diary.food.FoodListFindResponseDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterFindResponseDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Writer;

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

    /*
    일지 관련 조회
     */
    @GetMapping("api/diary/owner/diabetes-diary/{id}")
    public ApiResult<WriterFindResponseDTO> findOwnerOfDiary(@PathVariable("id") Long diaryId) {
        logger.info("find owner of the diary");

        return ApiResult.OK(new WriterFindResponseDTO(findDiaryService.getWriterOfDiary(EntityId.of(DiabetesDiary.class, diaryId))));
    }

    @GetMapping("api/diary/owner/{id}/diabetes-diary/list")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiabetesDiariesOfOwner(@PathVariable("id") Long writerId) {
        logger.info("find diabetes diaries of the owner");

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, writerId));
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}")
    public ApiResult<DiabetesDiaryFindResponseDTO> findOneDiabetesDiaryOfOwner(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find only one diabetes diary of the owner");

        return ApiResult.OK(new DiabetesDiaryFindResponseDTO(findDiaryService.getDiabetesDiaryOfWriter(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId))));
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}/with/relations")
    public ApiResult<DiaryFetchResponseDTO> findOneDiabetesDiaryOfOwnerWithRelation(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find One Diabetes Diary Of Owner With Relation");

        return ApiResult.OK(new DiaryFetchResponseDTO(findDiaryService.getDiabetesDiaryOfWriterWithRelation(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId))));
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiariesBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Diaries Between Time ->" + startDate + " : " + endDate);

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiariesBetweenTime(EntityId.of(Writer.class, writerId), startDate, endDate);
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/ge/{fastingPlasmaGlucose}")
    public ApiResult<List<DiaryListFindResponseDTO>> findFpgHigherOrEqual(@PathVariable("writerId") Long writerId, @PathVariable("fastingPlasmaGlucose") int fastingPlasmaGlucose) {
        logger.info("find Fpg HigherOrEqual -> " + fastingPlasmaGlucose);

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getFpgHigherOrEqual(EntityId.of(Writer.class, writerId), fastingPlasmaGlucose);
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/le/{fastingPlasmaGlucose}")
    public ApiResult<List<DiaryListFindResponseDTO>> findFpgLowerOrEqual(@PathVariable("writerId") Long writerId, @PathVariable("fastingPlasmaGlucose") int fastingPlasmaGlucose) {
        logger.info("find Fpg LowerOrEqual -> " + fastingPlasmaGlucose);

        List<DiabetesDiary> diaries = findDiaryService.getFpgLowerOrEqual(EntityId.of(Writer.class, writerId), fastingPlasmaGlucose);
        List<DiaryListFindResponseDTO> dtoList = diaries.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /*
    식단 관련 조회
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}/diet/list")
    public ApiResult<List<DietListFindResponseDTO>> findDietsOfDiary(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find Diets Of Diary");

        List<Diet> dietList = findDiaryService.getDietsOfDiary(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId));
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}/diet/{dietId}")
    public ApiResult<DietFindResponseDTO> findOneDietOfDiary(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId, @PathVariable("dietId") Long dietId) {
        logger.info("find One Diet Of Diary");

        return ApiResult.OK(new DietFindResponseDTO(findDiaryService.getOneDietOfDiary(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EntityId.of(Diet.class, dietId))));
    }

    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DietListFindResponseDTO>> findHigherThanBloodSugarBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Higher Than BloodSugar Between Time");

        List<Diet> dietList = findDiaryService.getHigherThanBloodSugarBetweenTime(EntityId.of(Writer.class, writerId), bloodSugar, startDate, endDate);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diet/le/{bloodSugar}/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DietListFindResponseDTO>> findLowerThanBloodSugarBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Lower Than BloodSugar Between Time");

        List<Diet> dietList = findDiaryService.getLowerThanBloodSugarBetweenTime(EntityId.of(Writer.class, writerId), bloodSugar, startDate, endDate);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/eat-time/{eatTime}")
    public ApiResult<List<DietListFindResponseDTO>> findHigherThanBloodSugarInEatTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("eatTime") EatTime eatTime) {
        logger.info("find Higher Than BloodSugar In EatTime");

        List<Diet> dietList = findDiaryService.getHigherThanBloodSugarInEatTime(EntityId.of(Writer.class, writerId), bloodSugar, eatTime);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diet/le/{bloodSugar}/eat-time/{eatTime}")
    public ApiResult<List<DietListFindResponseDTO>> findLowerThanBloodSugarInEatTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("eatTime") EatTime eatTime) {
        logger.info("find Lower Than BloodSugar In EatTime");

        List<Diet> dietList = findDiaryService.getLowerThanBloodSugarInEatTime(EntityId.of(Writer.class, writerId), bloodSugar, eatTime);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diet/average")
    public ApiResult<Double> findAverageBloodSugarOfDiet(@PathVariable("writerId") Long writerId) {
        logger.info("find Average BloodSugar Of Diet");

        return ApiResult.OK(findDiaryService.getAverageBloodSugarOfDiet(EntityId.of(Writer.class, writerId)));
    }

    /*
    음식 조회 관련 메서드들
     */

    @GetMapping("api/diary/owner/{writerId}/diet/{dietId}/food/list")
    public ApiResult<List<FoodListFindResponseDTO>> findFoodsInDiet(@PathVariable("writerId") Long writerId, @PathVariable("dietId") Long dietId) {
        logger.info("find Foods In Diet");

        List<Food> foodList = findDiaryService.getFoodsInDiet(EntityId.of(Writer.class, writerId), EntityId.of(Diet.class, dietId));
        List<FoodListFindResponseDTO> dtoList = foodList.stream().map(
                FoodListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    @GetMapping("api/diary/owner/{writerId}/diet/{dietId}/food/{foodId}")
    public ApiResult<FoodFindResponseDTO> findOneFoodByIdInDiet(@PathVariable("writerId") Long writerId, @PathVariable("dietId") Long dietId, @PathVariable("foodId") Long foodId) {
        logger.info("find One Food By Id In Diet");

        return ApiResult.OK(new FoodFindResponseDTO(findDiaryService.getOneFoodByIdInDiet(EntityId.of(Writer.class, writerId), EntityId.of(Diet.class, dietId), EntityId.of(Food.class, foodId))));
    }

    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/food/names")
    public ApiResult<List<String>> findFoodNamesInDietHigherThanBloodSugar(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar) {
        logger.info("find Food Names In Diet Higher Than BloodSugar");

        return ApiResult.OK(findDiaryService.getFoodNamesInDietHigherThanBloodSugar(EntityId.of(Writer.class, writerId), bloodSugar));
    }

    @GetMapping("api/diary/owner/{writerId}/diet/ge/average/food/names")
    public ApiResult<List<String>> findFoodHigherThanAverageBloodSugarOfDiet(@PathVariable("writerId") Long writerId) {
        logger.info("find Food Higher Than Average BloodSugar Of Diet");

        return ApiResult.OK(findDiaryService.getFoodHigherThanAverageBloodSugarOfDiet(EntityId.of(Writer.class, writerId)));
    }
}
