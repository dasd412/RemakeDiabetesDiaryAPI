/*
 * @(#)DiaryFindRestController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.without_security;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.without_security.dto.diabetesdiary.DiaryFetchResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import com.dasd412.remake.api.controller.without_security.dto.diabetesdiary.DiabetesDiaryFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.diabetesdiary.DiaryListFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.diet.DietFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.diet.DietListFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.food.FoodFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.food.FoodListFindResponseDTO;
import com.dasd412.remake.api.controller.without_security.dto.writer.WriterFindResponseDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Writer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 일지 조회와 관련된 일을 처리하는 RestController. 단, 시큐리티 적용 전에 작성된 클래스이기 때문에
 * 시큐리티가 적용된 지금은 "테스트"용으로만 사용된다. Admin 권한을 갖고 있는 사람만 접근할 수 있다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

@RestController
public class DiaryFindRestController {
    /* 주의점

    Missing URI template variable for method parameter of type string 관련 에러는
    @GetMapping() 내의 id 값과 파라미터로 들어가는 id가 이름이 서로 다를 경우에 발생한다.
    해결 방법 중 하나는 @PathVariable()에 변수를 할당하는 것이다.

    ex)
    @GetMapping("/{userId}")
    ->@PathVariable("userId") Long id
     */

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FindDiaryService findDiaryService;

    public DiaryFindRestController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    /**
     * 일지 id를 받으면, 해당 일지를 쓴 사람의 정보 반환
     *
     * @param diaryId 일지 id
     * @return 일지를 쓴 사람의 정보
     */
    @GetMapping("api/diary/owner/diabetes-diary/{id}")
    public ApiResult<WriterFindResponseDTO> findOwnerOfDiary(@PathVariable("id") Long diaryId) {
        logger.info("find owner of the diary");

        return ApiResult.OK(new WriterFindResponseDTO(findDiaryService.getWriterOfDiary(EntityId.of(DiabetesDiary.class, diaryId))));
    }

    /**
     * @param writerId 작성자 id
     * @return 작성자가 작성한 모든 일지
     */
    @GetMapping("api/diary/owner/{id}/diabetes-diary/list")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiabetesDiariesOfOwner(@PathVariable("id") Long writerId) {
        logger.info("find diabetes diaries of the owner");

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiabetesDiariesOfWriter(EntityId.of(Writer.class, writerId));
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @return 작성자가 썼던 해당 id의 일지 "하나" 반환
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}")
    public ApiResult<DiabetesDiaryFindResponseDTO> findOneDiabetesDiaryOfOwner(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find only one diabetes diary of the owner");

        return ApiResult.OK(new DiabetesDiaryFindResponseDTO(findDiaryService.getDiabetesDiaryOfWriter(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId))));
    }

    /**
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @return FetchJoin 적용해서 작성자가 작성한 일지와 관련된 모든 엔티티 반환
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}/with/relations")
    public ApiResult<DiaryFetchResponseDTO> findOneDiabetesDiaryOfOwnerWithRelation(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId) {
        logger.info("find One Diabetes Diary Of Owner With Relation");

        return ApiResult.OK(new DiaryFetchResponseDTO(findDiaryService.getDiabetesDiaryOfWriterWithRelation(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId))));
    }

    /**
     * @param writerId  작성자 id
     * @param startDate 시작 날짜
     * @param endDate   끝 날짜
     * @return 해당 기간에 작성된 모든 일지
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DiaryListFindResponseDTO>> findDiariesBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Diaries Between Time ->" + startDate + " : " + endDate);

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getDiariesBetweenTime(EntityId.of(Writer.class, writerId), startDate, endDate);
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId             작성자 id
     * @param fastingPlasmaGlucose 공복 혈당 수치
     * @return 공복 혈당 수치 입력보다 높거나 같은 혈당 일지들 반환
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/ge/{fastingPlasmaGlucose}")
    public ApiResult<List<DiaryListFindResponseDTO>> findFpgHigherOrEqual(@PathVariable("writerId") Long writerId, @PathVariable("fastingPlasmaGlucose") int fastingPlasmaGlucose) {
        logger.info("find Fpg HigherOrEqual -> " + fastingPlasmaGlucose);

        List<DiabetesDiary> diabetesDiaryList = findDiaryService.getFpgHigherOrEqual(EntityId.of(Writer.class, writerId), fastingPlasmaGlucose);
        List<DiaryListFindResponseDTO> dtoList = diabetesDiaryList.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId             작성자 id
     * @param fastingPlasmaGlucose 공복 혈당 수치
     * @return 공복 혈당 수치 입력보다 낮거나 같은 혈당 일지들 반환
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/le/{fastingPlasmaGlucose}")
    public ApiResult<List<DiaryListFindResponseDTO>> findFpgLowerOrEqual(@PathVariable("writerId") Long writerId, @PathVariable("fastingPlasmaGlucose") int fastingPlasmaGlucose) {
        logger.info("find Fpg LowerOrEqual -> " + fastingPlasmaGlucose);

        List<DiabetesDiary> diaries = findDiaryService.getFpgLowerOrEqual(EntityId.of(Writer.class, writerId), fastingPlasmaGlucose);
        List<DiaryListFindResponseDTO> dtoList = diaries.stream().map(
                DiaryListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }


    /**
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @return 혈당 일지 내의 식단 정보들
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

    /**
     * @param writerId 작성자 id
     * @param diaryId  일지 id
     * @param dietId   식단 id
     * @return 위 id 값과 일치하는 식단 정보 하나 반환
     */
    @GetMapping("api/diary/owner/{writerId}/diabetes-diary/{diaryId}/diet/{dietId}")
    public ApiResult<DietFindResponseDTO> findOneDietOfDiary(@PathVariable("writerId") Long writerId, @PathVariable("diaryId") Long diaryId, @PathVariable("dietId") Long dietId) {
        logger.info("find One Diet Of Diary");

        return ApiResult.OK(new DietFindResponseDTO(findDiaryService.getOneDietOfDiary(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EntityId.of(Diet.class, dietId))));
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @param startDate  시작 날짜
     * @param endDate    끝 날짜
     * @return 해당 기간 내에 입력 혈당보다 높거나 같은 식단들 정보
     */
    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DietListFindResponseDTO>> findHigherThanBloodSugarBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Higher Than BloodSugar Between Time");

        List<Diet> dietList = findDiaryService.getHigherThanBloodSugarBetweenTime(EntityId.of(Writer.class, writerId), bloodSugar, startDate, endDate);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @param startDate  시작 날짜
     * @param endDate    끝 날짜
     * @return 해당 기간 내에 입력 혈당보다 낮거나 같은 식단들 정보
     */
    @GetMapping("api/diary/owner/{writerId}/diet/le/{bloodSugar}/start-date/{startDate}/end-date/{endDate}")
    public ApiResult<List<DietListFindResponseDTO>> findLowerThanBloodSugarBetweenTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("startDate") String startDate, @PathVariable("endDate") String endDate) {
        logger.info("find Lower Than BloodSugar Between Time");

        List<Diet> dietList = findDiaryService.getLowerThanBloodSugarBetweenTime(EntityId.of(Writer.class, writerId), bloodSugar, startDate, endDate);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @param eatTime    식사 시간
     * @return 식사시간 중 입력 혈당보다 높거나 같은 식단 정보들
     */
    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/eat-time/{eatTime}")
    public ApiResult<List<DietListFindResponseDTO>> findHigherThanBloodSugarInEatTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("eatTime") EatTime eatTime) {
        logger.info("find Higher Than BloodSugar In EatTime");

        List<Diet> dietList = findDiaryService.getHigherThanBloodSugarInEatTime(EntityId.of(Writer.class, writerId), bloodSugar, eatTime);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @param eatTime    식사 시간
     * @return 식사시간 중 입력 혈당보다 낮거나 같은 식단 정보들
     */
    @GetMapping("api/diary/owner/{writerId}/diet/le/{bloodSugar}/eat-time/{eatTime}")
    public ApiResult<List<DietListFindResponseDTO>> findLowerThanBloodSugarInEatTime(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar, @PathVariable("eatTime") EatTime eatTime) {
        logger.info("find Lower Than BloodSugar In EatTime");

        List<Diet> dietList = findDiaryService.getLowerThanBloodSugarInEatTime(EntityId.of(Writer.class, writerId), bloodSugar, eatTime);
        List<DietListFindResponseDTO> dtoList = dietList.stream().map(
                DietListFindResponseDTO::new
        ).collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }

    /**
     * @param writerId 작성자 id
     * @return 작성자의 평균 "식단" 혈당
     */
    @GetMapping("api/diary/owner/{writerId}/diet/average")
    public ApiResult<Double> findAverageBloodSugarOfDiet(@PathVariable("writerId") Long writerId) {
        logger.info("find Average BloodSugar Of Diet");

        return ApiResult.OK(findDiaryService.getAverageBloodSugarOfDiet(EntityId.of(Writer.class, writerId)));
    }

    /**
     * @param writerId 작성자 id
     * @param dietId   식단 id
     * @return 작성자의 식단 내 음식들
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

    /**
     * @param writerId 작성자 id
     * @param dietId   식단 id
     * @param foodId   음식 id
     * @return 입력에 해당하는 음식 한 개
     */
    @GetMapping("api/diary/owner/{writerId}/diet/{dietId}/food/{foodId}")
    public ApiResult<FoodFindResponseDTO> findOneFoodByIdInDiet(@PathVariable("writerId") Long writerId, @PathVariable("dietId") Long dietId, @PathVariable("foodId") Long foodId) {
        logger.info("find One Food By Id In Diet");

        return ApiResult.OK(new FoodFindResponseDTO(findDiaryService.getOneFoodByIdInDiet(EntityId.of(Writer.class, writerId), EntityId.of(Diet.class, dietId), EntityId.of(Food.class, foodId))));
    }

    /**
     * @param writerId   작성자 id
     * @param bloodSugar 식단 혈당
     * @return 식단 혈당보다 높거나 같은 식단에서 기재된 음식들
     */
    @GetMapping("api/diary/owner/{writerId}/diet/ge/{bloodSugar}/food/names")
    public ApiResult<List<String>> findFoodNamesInDietHigherThanBloodSugar(@PathVariable("writerId") Long writerId, @PathVariable("bloodSugar") int bloodSugar) {
        logger.info("find Food Names In Diet Higher Than BloodSugar");

        return ApiResult.OK(findDiaryService.getFoodNamesInDietHigherThanBloodSugar(EntityId.of(Writer.class, writerId), bloodSugar));
    }

    /**
     * @param writerId 작성자 id
     * @return 식단의 평균혈당 보다 높거나 같은 식단에서 기재된 음식들
     */
    @GetMapping("api/diary/owner/{writerId}/diet/ge/average/food/names")
    public ApiResult<List<String>> findFoodHigherThanAverageBloodSugarOfDiet(@PathVariable("writerId") Long writerId) {
        logger.info("find Food Higher Than Average BloodSugar Of Diet");

        return ApiResult.OK(findDiaryService.getFoodHigherThanAverageBloodSugarOfDiet(EntityId.of(Writer.class, writerId)));
    }
}
