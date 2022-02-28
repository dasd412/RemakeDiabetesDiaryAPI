/*
 * @(#)ProfileControllerTest.java        1.1.1 2022/2/28
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.TestUserDetailsService;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodDTO;
import com.dasd412.remake.api.controller.security.domain_view.dto.ProfileResponseDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.profile.ProfileRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 양영준
 * @version 1.1.1 2022년 2월 28일
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ProfileControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private ProfileRepository profileRepository;

    private final TestUserDetailsService testUserDetailsService = new TestUserDetailsService();

    private PrincipalDetails principalDetails;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setup() throws Exception {
        logger.info("set up");
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        //Writer[id=1,name=user@example.com,email=user@example.com,role=User]
        Writer me = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(TestUserDetailsService.USERNAME)
                .email(TestUserDetailsService.USERNAME)
                .password("test")
                .role(Role.User)
                .provider(null)
                .providerId(null)
                .build();

        writerRepository.save(me);
        principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestUserDetailsService.USERNAME);

        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i))
                .collect(Collectors.toList());

        SecurityDiaryPostRequestDTO dto = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(0).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .breakFastSugar(0).lunchSugar(0).dinnerSugar(0)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        //when and then
        mockMvc.perform(post("/api/diary/user/diabetes-diary").with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"));
    }

    @After
    public void clean() {
        logger.info("clean\n");
        writerRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    public void makeProfile() throws Exception {
        //given
        String url = "/profile";

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        List<Writer> writers = writerRepository.findAll();
        assertThat(writers.size()).isEqualTo(1);
        assertThat(writers.get(0).getProfile().getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);

        List<Profile> profileList = profileRepository.findAll();
        assertThat(profileList.size()).isEqualTo(1);
        assertThat(profileList.get(0).getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);
    }

    @Test
    public void findProfile() throws Exception {
        //given
        String url = "/profile";
        //처음에 프로필이 없을 경우엔 make profile (기본 당뇨 단계는 NORMAL)
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        //when and then (프로필이 있을 경우엔 find profile)
        MvcResult result = mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk()).andReturn();

        ProfileResponseDTO dto = (ProfileResponseDTO) Objects.requireNonNull(result.getModelAndView()).getModel().get("profile");
        assertThat(dto.getDiabetesPhase()).isEqualTo(DiabetesPhase.NORMAL);
    }

    @Test
    public void updateProfile() throws Exception {

    }

    /*
    회원 탈퇴 테스트
     */
    @Test
    public void withDraw() throws Exception {

    }
}
