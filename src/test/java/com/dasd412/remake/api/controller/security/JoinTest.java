/*
 * @(#)JoinTest.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.controller.security.writer.UserJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 회원 가입과 관련된 테스트 수행
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class JoinTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("before", "before", "before@naver.com");
        String url = "http://localhost:" + port + "/signup/user";

        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    public void signUp() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("test", "test", "test@naver.com");
        String url = "http://localhost:" + port + "/signup/user";

        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"));

        Writer writer = writerRepository.findWriterByName("test").orElseThrow(NoResultException::new);
        assertThat(writer.getName()).isEqualTo(dto.getName());
        assertThat(writer.getEmail()).isEqualTo(dto.getEmail());
    }

    @Test
    public void duplicateName() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("before", "before", "duplicateName@naver.com");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("duplicateName"));
    }

    @Test
    public void duplicateEmail() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("testEmail", "before", "before@naver.com");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.error.message").value("duplicateEmail"));
    }

    @Test
    public void emptyName() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("", "empty", "before@naver.com");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void emptyPassword() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "", "before@naver.com");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void emptyEmail() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "pp", "");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail() throws Exception {
        //given
        UserJoinRequestDTO dto = new UserJoinRequestDTO("name", "pp", "teasesatas");
        String url = "http://localhost:" + port + "/signup/user";
        //when and then
        mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}

