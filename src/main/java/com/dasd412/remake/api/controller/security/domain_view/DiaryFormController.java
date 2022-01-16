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

@Controller
public class DiaryFormController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FindDiaryService findDiaryService;

    public DiaryFormController(FindDiaryService findDiaryService) {
        this.findDiaryService = findDiaryService;
    }

    @GetMapping("/post")
    public String postForm(@RequestParam(value = "year") String year, @RequestParam(value = "month") String month, @RequestParam(value = "day") String day, Model model) {
        logger.info("post view resolve");
        logger.info(year + ":" + month + ":" + day);

        model.addAttribute("year", year);
        model.addAttribute("month", month);
        model.addAttribute("day", day);

        return "post/post";
    }

    @GetMapping("/update-delete/{diaryId}")
    public String updateDeleteForm(@AuthenticationPrincipal PrincipalDetails principalDetails, @PathVariable Long diaryId, Model model) {
        logger.info("update delete view resolve");

        //fetch join 을 이용하여 일지와 관련된 모든 엔티티를 "한꺼번에" 불러옵니다. (n+1 방지)
        DiabetesDiary targetDiary = findDiaryService.getDiabetesDiaryOfWriterWithRelation(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, diaryId));
        PostForUpdateDTO dto = new PostForUpdateDTO(targetDiary);
        logger.info(dto.toString());

        model.addAttribute("diary", dto);
        return "post/update-delete";
    }

    @GetMapping("/update-delete/404")
    public String errorPathResolve() {
        logger.info("error path view resolve for solving circular view path");
        return "error/404";
    }
}
