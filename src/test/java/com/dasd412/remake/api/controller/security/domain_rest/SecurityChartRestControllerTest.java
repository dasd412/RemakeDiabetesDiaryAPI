/*
 * @(#)SecurityChartRestControllerTest.java        1.0.3 2022/2/1
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author 양영준
 * @version 1.0.3 2022년 2월 1일
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SecurityChartRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

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
        Writer entity = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(TestUserDetailsService.USERNAME)
                .email(TestUserDetailsService.USERNAME)
                .password("test")
                .role(Role.User)
                .provider(null)
                .providerId(null)
                .build();

        writerRepository.save(entity);
        principalDetails = (PrincipalDetails) testUserDetailsService.loadUserByUsername(TestUserDetailsService.USERNAME);

        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i))
                .collect(Collectors.toList());

        SecurityDiaryPostRequestDTO dto = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(100).remark("test")
                .year("2022").month("01").day("29").hour("00").minute("00").second("00")
                .breakFastSugar(110).lunchSugar(120).dinnerSugar(130)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        String url = "/api/diary/user/diabetes-diary";

        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1));

        SecurityDiaryPostRequestDTO dto2 = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(110).remark("test")
                .year("2022").month("01").day("31").hour("00").minute("00").second("00")
                .breakFastSugar(110).lunchSugar(120).dinnerSugar(130)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto2)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(2));

        logger.info("set end \n");
    }

    @After
    public void clean() {
        logger.info("clean\n");
        writerRepository.deleteAll();
    }

    @Test
    public void findAllFpg() throws Exception {
        //given
        String url = "/chart-menu/fasting-plasma-glucose/all";

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(2)));

    }

    @Test
    public void findFpgBetweenTime() throws Exception {
        String url = "/chart-menu/fasting-plasma-glucose/between";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2022");
        params.add("startMonth", "01");
        params.add("startDay", "28");

        params.add("endYear", "2022");
        params.add("endMonth", "01");
        params.add("endDay", "30");

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8).params(params))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(1)));
    }

    @Test
    public void findAllBloodSugar() throws Exception {
        //given
        String url = "/chart-menu/blood-sugar/all";

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response").value(hasSize(6)));
    }

    @Test
    public void findBloodSugarBetween() throws Exception {
        //given
        String url = "/chart-menu/blood-sugar/between";
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("startYear", "2022");
        params.add("startMonth", "01");
        params.add("startDay", "28");

        params.add("endYear", "2022");
        params.add("endMonth", "01");
        params.add("endDay", "30");

        //when and then
        mockMvc.perform(get(url).with(user(principalDetails)).params(params).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }

    @Test
    public void findAverageAll() throws Exception {
        //given
        String url = "/chart-menu/average/all";

        //when and then
        MvcResult rst=mockMvc.perform(get(url).with(user(principalDetails)).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.averageFpg").value(105.0))
                .andExpect(jsonPath("$.response.averageBreakFast").value(110.0))
                .andExpect(jsonPath("$.response.averageLunch").value(120.0))
                .andExpect(jsonPath("$.response.averageBloodSugar").value(120.0))
                .andReturn();

    }
}