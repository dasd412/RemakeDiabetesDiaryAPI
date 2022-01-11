package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.controller.security.domain_rest.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.SecurityDiaryPostResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.dasd412.remake.api.service.domain.UpdateDeleteDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
public class SecurityDiaryRestController {
    //시큐리티에서는 인증이 이미 되있기 때문에 기존 url 은 관리자만 진입가능하게 바꿨다.
    private final SaveDiaryService saveDiaryService;

    private final UpdateDeleteDiaryService updateDeleteDiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public SecurityDiaryRestController(SaveDiaryService saveDiaryService, UpdateDeleteDiaryService updateDeleteDiaryService) {
        this.saveDiaryService = saveDiaryService;
        this.updateDeleteDiaryService = updateDeleteDiaryService;
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
                .forEach(elem -> {
                    saveDiaryService.saveFoodAndAmountOfWriterById
                            (EntityId.of(Writer.class, writerId),
                                    EntityId.of(DiabetesDiary.class, diaryId),
                                    EntityId.of(Diet.class, breakFast.getDietId()),
                                    elem.getFoodName(), elem.getAmount()
                            );
                });

        dto.getLunchFoods()
                .forEach(elem -> {
                    saveDiaryService.saveFoodAndAmountOfWriterById
                            (EntityId.of(Writer.class, writerId),
                                    EntityId.of(DiabetesDiary.class, diaryId),
                                    EntityId.of(Diet.class, lunch.getDietId()),
                                    elem.getFoodName(), elem.getAmount()
                            );
                });

        dto.getDinnerFoods()
                .forEach(elem -> {
                    saveDiaryService.saveFoodAndAmountOfWriterById
                            (EntityId.of(Writer.class, writerId),
                                    EntityId.of(DiabetesDiary.class, diaryId),
                                    EntityId.of(Diet.class, dinner.getDietId()),
                                    elem.getFoodName(), elem.getAmount()
                            );
                });
        return ApiResult.OK(new SecurityDiaryPostResponseDTO(diaryId));
    }

    @PutMapping("/api/diary/user/diabetes-diary/{diaryId}")
    public void updateDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId) {
        logger.info("update diabetes diary from browser");

    }

    @DeleteMapping("/api/diary/user/diabetes-diary/{diaryId}")
    public void bulkDeleteDiary(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId) {
        logger.info("bulk delete Diabetes Diary from browser");
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, diaryId));
    }
}
