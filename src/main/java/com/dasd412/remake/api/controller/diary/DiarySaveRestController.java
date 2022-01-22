/*
 * @(#)DiarySaveRestController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.controller.ApiResult;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryRequestDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryResponseDTO;
import com.dasd412.remake.api.controller.diary.diet.DietRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietResponseDTO;
import com.dasd412.remake.api.controller.diary.food.FoodRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodResponseDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterJoinRequestDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterJoinResponseDTO;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 일지 작성과 관련된 일을 처리하는 RestController. 단, 시큐리티 적용 전에 작성된 클래스이기 때문에
 * 시큐리티가 적용된 지금은 "테스트"용으로만 사용된다. Admin 권한을 갖고 있는 사람만 접근할 수 있다.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

@RestController
public class DiarySaveRestController {


    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final SaveDiaryService saveDiaryService;

    public DiarySaveRestController(SaveDiaryService saveDiaryService) {
        this.saveDiaryService = saveDiaryService;
    }

    /**
     * @param dto 작성자 회원 가입 정보
     * @return 회원가입된 정보
     */
    @PostMapping("api/diary/writer")
    public ApiResult<WriterJoinResponseDTO> joinWriter(@RequestBody WriterJoinRequestDTO dto) {
        logger.info("join writer : " + dto.toString());
        return ApiResult.OK(new WriterJoinResponseDTO(saveDiaryService.saveWriter(dto.getName(), dto.getEmail(), dto.getRole())));
    }

    /**
     * @param dto 일지에 기재된 내용
     * @return 일지가 제대로 작성됬는 지 여부와 일지 id
     */
    @PostMapping("api/diary/diabetes-diary")
    public ApiResult<DiabetesDiaryResponseDTO> postDiary(@RequestBody DiabetesDiaryRequestDTO dto) {
        logger.info("post diary : " + dto.toString());

        /*컨트롤러 테스트 시, LocalDateTime->JSON 으로 직렬화를 못한다. 그걸 해결하기 위한 코드 두 줄. */
        String date = dto.getYear() + "-" + dto.getMonth() + "-" + dto.getDay() + " " + dto.getHour() + ":" + dto.getMinute() + ":" + dto.getSecond();
        LocalDateTime writtenTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        logger.info("writtenTime : " + writtenTime);

        return ApiResult.OK(new DiabetesDiaryResponseDTO(saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, dto.getWriterId()), dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime)));
    }

    /**
     * @param dto 식단에 기재된 정보
     * @return 식단이 제대로 작성됬는 지 여부와 식단 id
     */
    @PostMapping("api/diary/diet")
    public ApiResult<DietResponseDTO> postDiet(@RequestBody DietRequestDTO dto) {
        logger.info("post Diet : " + dto.toString());
        return ApiResult.OK(new DietResponseDTO(saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, dto.getWriterId()), EntityId.of(DiabetesDiary.class, dto.getDiaryId()), dto.getEatTime(), dto.getBloodSugar())));
    }

    /**
     * @param dto 음식에 기재된 정보
     * @return 음식이 제대로 작성됬는 지 여부와 음식 id
     */
    @PostMapping("api/diary/food")
    public ApiResult<FoodResponseDTO> postFood(@RequestBody FoodRequestDTO dto) {
        logger.info("post Food : " + dto.toString());
        return ApiResult.OK(new FoodResponseDTO(saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, dto.getWriterId()), EntityId.of(DiabetesDiary.class, dto.getDiaryId()), EntityId.of(Diet.class, dto.getDietId()), dto.getFoodName())));
    }

}
