/*
 * @(#)PropertiesProfileController.java        1.2.0 2022/3/24
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

/**
 * 현재 배포 운영 상에서 사용중인 프로파일 확인용
 *
 * @author 양영준
 * @version 1.2.0 2022년 3월 24일
 */
@RestController
public class PropertiesProfileController {
    private final Environment environment;

    public PropertiesProfileController(Environment environment) {
        this.environment = environment;
    }

    @GetMapping("/properties")
    public String getProfileOfProperties() {
        List<String> profiles = Arrays.asList(environment.getActiveProfiles());
        List<String> realProfiles = Arrays.asList("real", "real1", "real2");

        String defaultProfile = profiles.isEmpty() ? "default" : profiles.get(0);

        return profiles.stream()
                .filter(realProfiles::contains)
                .findAny()
                .orElse(defaultProfile);
    }
}
