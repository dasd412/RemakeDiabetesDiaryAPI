/*
 * @(#)FindInfoService.java        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.controller.exception.OAuthFindUsernameException;
import com.dasd412.remake.api.domain.diary.writer.QWriter;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.util.RegexChecker;
import com.querydsl.core.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import static com.google.common.base.Preconditions.*;

/**
 * 아이디 찾기 등 유저 정보 검색을 담당하는 서비스
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 2일
 */
@Service
public class FindInfoService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;

    public FindInfoService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    /**
     * @param email 사용자 이메일
     * @return 이메일에 해당하는 사용자 id 정보
     */
    public String getUserNameByEmail(String email) {
        logger.info("get user name");
        checkArgument(RegexChecker.isRightEmail(email), "String must be pattern of email!!");

        Tuple tuple = writerRepository.findUserInfoByEmail(email);

        if (tuple == null) {
            throw new UsernameNotFoundException("해당하는 유저 id가 존재하지 않아요!");
        }
        String provider = tuple.get(QWriter.writer.provider);

        /*
        provider != null ->  OAuth 회원가입 유저. id, password 찾을 필요 없음.
        */
        if (provider != null) {
            throw new OAuthFindUsernameException("OAuth 회원 가입 사용자는 id를 찾을 필요가 없어요.");
        } else {
            String userName = tuple.get(QWriter.writer.name);
            if (userName == null) {
                throw new UsernameNotFoundException("해당하는 유저 id가 존재하지 않아요!");
            }

            return userName;
        }
    }

}
