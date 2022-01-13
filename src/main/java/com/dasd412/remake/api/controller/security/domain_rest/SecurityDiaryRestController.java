package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.security.domain_rest.dto.*;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.dasd412.remake.api.service.domain.UpdateDeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SecurityDiaryRestController {
    //시큐리티에서는 인증이 이미 되있기 때문에 기존 url 은 관리자만 진입가능하게 바꿨다.
    private final SaveDiaryService saveDiaryService;

    private final UpdateDeleteDiaryService updateDeleteDiaryService;

    private final FindDiaryService findDiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService, UpdateDeleteDiaryService updateDeleteDiaryService, FindDiaryService findDiaryService) {
        this.saveDiaryService = saveDiaryService;
        this.updateDeleteDiaryService = updateDeleteDiaryService;
        this.findDiaryService = findDiaryService;
    }

    @PostMapping("/api/diary/user/diabetes-diary")
    public ApiResult<SecurityDiaryPostResponseDTO> postDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody @Valid SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary with authenticated user");
        Long writerId = principalDetails.getWriter().getId();
        //JSON 직렬화가 LocalDateTime 에는 적용이 안되서 작성한 코드.
        String date = dto.getYear() + "-" + dto.getMonth() + "-" + dto.getDay() + " " + dto.getHour() + ":" + dto.getMinute() + ":" + dto.getSecond();
        LocalDateTime writtenTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, writerId), dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);
        Long diaryId = diary.getId();
        //식단 저장
        Diet breakFast = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.BreakFast, dto.getBreakFastSugar());
        Diet lunch = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.Lunch, dto.getLunchSugar());
        Diet dinner = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, writerId), EntityId.of(DiabetesDiary.class, diaryId), EatTime.Dinner, dto.getDinnerSugar());

        //음식 저장
        dto.getBreakFastFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, breakFast.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getLunchFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, lunch.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getDinnerFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (EntityId.of(Writer.class, writerId),
                                EntityId.of(DiabetesDiary.class, diaryId),
                                EntityId.of(Diet.class, dinner.getDietId()),
                                elem.getFoodName(), elem.getAmount()
                        ));
        return ApiResult.OK(new SecurityDiaryPostResponseDTO(diaryId));
    }

    @PutMapping("/api/diary/user/diabetes-diary")
    public ApiResult<SecurityDiaryUpdateResponseDTO> updateDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody SecurityDiaryUpdateDTO dto) {
        logger.info("update diabetes diary from browser");
        logger.info(dto.toString());

        EntityId<Writer, Long> writerLongEntityId = EntityId.of(Writer.class, principalDetails.getWriter().getId());
        EntityId<DiabetesDiary, Long> diabetesDiaryLongEntityId = EntityId.of(DiabetesDiary.class, dto.getDiaryId());
        EntityId<Diet, Long> breakFastEntityId = EntityId.of(Diet.class, dto.getBreakFastId());
        EntityId<Diet, Long> lunchEntityId = EntityId.of(Diet.class, dto.getLunchId());
        EntityId<Diet, Long> dinnerEntityId = EntityId.of(Diet.class, dto.getDinnerId());

        //음식 변경 감지 되었으면 수정.
        if (dto.isDiaryDirty()) {
            updateDeleteDiaryService.updateDiary(writerLongEntityId, diabetesDiaryLongEntityId, dto.getFastingPlasmaGlucose(), dto.getRemark());
        }

        //식단 변경 감지 되었으면 수정
        if (dto.isBreakFastDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, breakFastEntityId, EatTime.BreakFast, dto.getBreakFastSugar());
        }

        if (dto.isLunchDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, lunchEntityId, EatTime.Lunch, dto.getLunchSugar());
        }

        if (dto.isDinnerDirty()) {
            updateDeleteDiaryService.updateDiet(writerLongEntityId, diabetesDiaryLongEntityId, dinnerEntityId, EatTime.Dinner, dto.getDinnerSugar());
        }

        //기존 음식 엔티티 삭제 ( in (id) 벌크 삭제)
        List<SecurityFoodForUpdateDTO> allOldFoods = new ArrayList<>();
        allOldFoods.addAll(dto.getOldBreakFastFoods());
        allOldFoods.addAll(dto.getOldLunchFoods());
        allOldFoods.addAll(dto.getOldDinnerFoods());

        if (allOldFoods.size()>0){
            List<EntityId<Food, Long>> foodEntityIds = allOldFoods.stream().map(elem -> EntityId.of(Food.class, elem.getId())).collect(Collectors.toList());
            updateDeleteDiaryService.bulkDeleteFoods(foodEntityIds);
        }

        //음식 엔티티 새로이 생성
        dto.getNewBreakFastFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                breakFastEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getNewLunchFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                lunchEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));

        dto.getNewDinnerFoods()
                .forEach(elem -> saveDiaryService.saveFoodAndAmountOfWriterById
                        (writerLongEntityId,
                                diabetesDiaryLongEntityId,
                                dinnerEntityId,
                                elem.getFoodName(), elem.getAmount()
                        ));
        return ApiResult.OK(new SecurityDiaryUpdateResponseDTO(dinnerEntityId.getId()));
    }

    @DeleteMapping("/api/diary/user/diabetes-diary/{diaryId}")
    public void bulkDeleteDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId) {
        logger.info("bulk delete Diabetes Diary from browser");
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, diaryId));
    }

    @GetMapping("/api/diary/user/diabetes-diary/list")
    public ApiResult<List<DiaryListBetweenTimeDTO>> getDiariesBetweenStartAndEnd(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                                                                 @RequestParam(value = "year") int year, @RequestParam(value = "month") int month,
                                                                                 @RequestParam(value = "startDay") int startDay,
                                                                                 @RequestParam(value = "endDay") int endDay) {
        logger.info("get diaries between time");
        LocalDateTime startDate = LocalDateTime.of(year, month, startDay, 0, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(year, month, endDay, 0, 0, 0);
        logger.info(startDate + " ~ " + endDate);

        //fetch join 안했음. 달력에서는 id 값과 작성 날짜만 필요하기 때문.
        List<DiabetesDiary> diaries =
                findDiaryService.getDiariesBetweenLocalDateTime(EntityId.of(Writer.class, principalDetails.getWriter().getId()),
                        startDate, endDate);

        //(일지 id, 작성 시간)의 형태의 dto
        List<DiaryListBetweenTimeDTO> dtoList = diaries
                .stream().map(DiaryListBetweenTimeDTO::new)
                .collect(Collectors.toList());

        return ApiResult.OK(dtoList);
    }
}
