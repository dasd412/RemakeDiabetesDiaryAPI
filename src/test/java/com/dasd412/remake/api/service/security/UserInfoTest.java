/*
 * @(#)UserInfoTest.java        1.1.2 2022/3/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */


package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.controller.exception.OAuthFindUsernameException;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 컨트롤러 레이어를 거치지 않고 유저의 정보 (프로필, 비밀 번호)등을 테스트하기 위한 클래스.
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 6일
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserInfoTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FindInfoService findInfoService;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private WriterService writerService;

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Transactional
    @Test
    public void incorrectFormatEmail() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("String must be pattern of email!!");

        //given
        String invalidEmail = "testString";

        //when
        findInfoService.getUserNameByEmail(invalidEmail);
    }

    @Transactional
    @Test
    public void noneUserName() {
        thrown.expect(UsernameNotFoundException.class);
        thrown.expectMessage("해당하는 유저 id가 존재하지 않아요!");

        //given
        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("TEST-NAME")
                .email("test@test.com")
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        findInfoService.getUserNameByEmail("incorrect@incorrect.com");
    }

    @Transactional
    @Test
    public void WhenNotAuthUser() {
        thrown.expect(OAuthFindUsernameException.class);
        thrown.expectMessage("OAuth 회원 가입 사용자는 id를 찾을 필요가 없어요.");

        //given
        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("TEST-NAME")
                .email("test@google.com")
                .provider("google")
                .providerId("1")
                .password(null)
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        findInfoService.getUserNameByEmail("test@google.com");
    }

    @Transactional
    @Test
    public void getUserNameByEmail() {
        //given
        String userName = "TEST-NAME";
        String email = "test@test.com";

        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(userName)
                .email(email)
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        String foundUserName = findInfoService.getUserNameByEmail(email);

        //then
        logger.info(foundUserName);
        assertThat(foundUserName).isEqualTo(userName);
    }


    @Transactional
    @Test
    public void existPasswordWhenOAuth() {
        //given
        String email = "test@google.com";
        String userName = "TEST-NAME";

        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(userName)
                .email(email)
                .provider("google")
                .providerId("1")
                .password(null)
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        boolean exist = writerRepository.existPassword(email, userName);

        //then
        assertThat(exist).isFalse();
    }

    @Transactional
    @Test
    public void existPasswordInService(){
        //given
        String email = "test@test.com";
        String userName = "TEST-NAME";

        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(userName)
                .email(email)
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);
        //when
        boolean exist = findInfoService.existPassword(email, userName);

        //then
        assertThat(exist).isTrue();
    }

    @Transactional
    @Test
    public void existPasswordInRepository() {
        //given
        String email = "test@test.com";
        String userName = "TEST-NAME";

        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(userName)
                .email(email)
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        boolean exist = writerRepository.existPassword(email, userName);

        //then
        assertThat(exist).isTrue();

    }


    @Test
    public void issueNewPassword() {
        //when and then
        String tempPassword = writerService.issueNewPassword();
        assertThat(tempPassword.length()).isEqualTo(10);
        logger.info(tempPassword);
    }

    @Test
    public void updateTempPassword() {
        //given
        String email = "test@test.com";
        String userName = "TEST-NAME";

        Writer writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(userName)
                .email(email)
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        String tempPassword = writerService.issueNewPassword();

        //when
        writerService.updateWithTempPassword(email, userName, tempPassword);

        //then
        Writer found = writerRepository.findAll().get(0);
        /*
         * bCryptPasswordEncoder의 경우 매번 해싱 값이 달라진다. 따라서 equals()로 비교할 수 없고
         * 직접 제공하는 matches()를 이용하여 비교해야 한다.
         */
        assertThat(bCryptPasswordEncoder.matches(tempPassword, found.getPassword())).isTrue();
    }


    @Test
    public void updateNewPassword() {
        //given
        String newPassword = "testPassword";
        EntityId<Writer, Long> writerEntityId = EntityId.of(Writer.class, 1L);

        Writer writer = Writer.builder()
                .writerEntityId(writerEntityId)
                .name("TEST-NAME")
                .email("test@test.com")
                .provider(null)
                .providerId(null)
                .password("test")
                .role(Role.User)
                .build();

        writerRepository.save(writer);

        //when
        writerService.updatePassword(writerEntityId, newPassword);

        //then
        Writer found = writerRepository.findAll().get(0);
        assertThat(bCryptPasswordEncoder.matches(newPassword, found.getPassword())).isTrue();
    }


}
