/*
 * @(#)ProfileController.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_view;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.ControllerViewPath;
import com.dasd412.remake.api.controller.security.domain_view.dto.ProfileResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.persistence.NoResultException;

@Controller
public class ProfileController {

    private final FindDiaryService findDiaryService;
    private final SaveDiaryService saveDiaryService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ProfileController(FindDiaryService findDiaryService, SaveDiaryService saveDiaryService) {
        this.findDiaryService = findDiaryService;
        this.saveDiaryService = saveDiaryService;
    }


    @GetMapping("/profile/view")
    public String profileViewResolve(@AuthenticationPrincipal PrincipalDetails principalDetails, Model model) {
        logger.info("profile view resolve");
        Profile profile;
        try {
            logger.info("find profile exist");
            profile = findDiaryService.getProfile(EntityId.of(Writer.class, principalDetails.getWriter().getId()));
        } catch (NoResultException e) {
            logger.info("make new profile");
            profile = saveDiaryService.makeProfile(EntityId.of(Writer.class, principalDetails.getWriter().getId()), DiabetesPhase.NORMAL);
        }

        ProfileResponseDTO dto = new ProfileResponseDTO(profile);
        logger.info("profile dto : " + dto);
        model.addAttribute("profile", dto);

        return ControllerViewPath.PROFILE;
    }
}
