/*
 * @(#)EmailService.java        1.1.2 2022/3/5
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.util.RegexChecker;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 이메일 발송을 담당하는 서비스
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 5일
 */
@Service
@AllArgsConstructor
public class EmailService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JavaMailSender mailSender;

    /**
     * @param email    목적지 이메일 주소
     * @param userName 찾은 id
     */
    public void sendEmailAboutId(String email, String userName) {
        logger.info("send email about id");
        checkArgument(RegexChecker.isRightEmail(email), "String must be pattern of email!!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("dasd412@naver.com");
        message.setSubject(email + "님의 id 보내드립니다. written by DiabetesDiaryAPI");
        message.setText("안녕하세요. 잃어버린 id는 다음과 같습니다. \n " + userName);

        mailSender.send(message);
    }

    /**
     * @param email        목적지 이메일 주소
     * @param tempPassword 새로 발급된 임시 비밀 번호
     */
    public void sendEmailAboutTempPassword(String email, String tempPassword) {
        logger.info("send temp password");
        checkArgument(RegexChecker.isRightEmail(email), "String must be pattern of email!!");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom("dasd412@naver.com");
        message.setSubject(email + "님의 임시 비밀 번호 보내드립니다. written by DiabetesDiaryAPI");
        message.setText("안녕하세요. 임시 비밀 번호는 다음과 같습니다. \n " + tempPassword);

        mailSender.send(message);

    }
}
