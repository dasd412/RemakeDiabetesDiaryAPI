/*
 * @(#)DiaryFormController.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_view;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_view.dto.PostForUpdateDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 일지 작성 폼 및 수정 삭제 폼 보여주는 컨트롤러
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Controller
public class DiaryFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FindDiaryService findDiaryService;

    public DiaryFormController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    /**
     * 날짜에 해당하는 정보를 모델에 담은 후, 일지 작성 폼 리턴
     *
     * @param year  년도
     * @param month 월
     * @param day   일
     * @param model 모델 객체
     * @return 일지 작성 폼
     */
    @GetMapping("/post")
    public String postForm(@RequestParam(value = "year") String year, @RequestParam(value = "month") String month, @RequestParam(value = "day") String day, Model model) {
        logger.info("post view resolve");
        logger.info(year + ":" + month + ":" + day);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);

        return "post/post";
    }

    /**
     * 일지 id에 해당하는 일지를 조회 후, 모델에 담아서 일지 수정 및 삭제 폼을 리턴
     *
     * @param principalDetails 사용자 로그인 정보
     * @param diaryId          일지 id
     * @param model            모델 객체
     * @return 일지 수정 및 삭제 폼
     */
    @GetMapping("/update-delete/{diaryId}")
    public String updateDeleteForm(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId, Model model) {
        logger.info("update delete view resolve");

        /* fetch join 을 이용하여 일지와 관련된 모든 엔티티를 "한꺼번에" 불러옵니다. (n+1 방지) */
        DiabetesDiary targetDiary = findDiaryService.getDiabetesDiaryOfWriterWithRelation(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, diaryId));

        PostForUpdateDTO dto = new PostForUpdateDTO(targetDiary);
        logger.info(dto.toString());

        model.addAttribute("diary", dto);

        return "post/update-delete";
    }

    /**
     * 순환 view path 해결을 위해서 만든 메서드.
     *
     * @return 404 에러 페이지
     */
    @GetMapping("/update-delete/404")
    public String errorPathResolve() {
        logger.info("error path view resolve for solving circular view path");
        return "error/404";
    }
}
