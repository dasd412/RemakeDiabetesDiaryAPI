/*
 * @(#)ByCryptConfig.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */
package com.dasd412.remake.api.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 암호화를 해주는 BCryptPasswordEncoder Bean을 주입하기 위한 클래스.
 * SecurityConfig 클래스에서 해당 빈을 주입하면 순환 참조 오류가 나온다. 이를 방지하기 위해 따로 만든 클래스이다.
 */
@Configuration
public class ByCryptConfig {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
