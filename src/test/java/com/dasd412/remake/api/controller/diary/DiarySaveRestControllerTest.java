/*
 * @(#)DiarySaveRestControllerTest.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.controller.diary;

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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodRequestDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.writer.Role;

import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Security 적용 없이 정보 저장과 관련된 컨트롤러 테스트 수행.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DiarySaveRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "Admin")
    public void joinWriterInvalidName() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        //when and then
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void joinWriter() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        //when and then
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.email").value("test@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value("Admin"));
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postDiaryInvalidFpg() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, -2000, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postDiaryInvalidRemark() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "01";
        String hour = "23";
        String minute = "49";
        String second = "59";

        StringBuilder remark = new StringBuilder();
        IntStream.range(0, 1000).forEach(i -> remark.append("a"));

        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 200, remark.toString(), year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postDiary() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 100, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.fastingPlasmaGlucose").value("100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.remark").value("TEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writtenTime").value("2021-09-25T06:49:41"));
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postDietInvalidBloodSugar() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 100, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"));

        //when and then
        DietRequestDTO dietRequestDTO = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200000);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postDiet() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 100, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"));

        //when and then
        DietRequestDTO dietRequestDTO = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.bloodSugar").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.eatTime").value("Lunch"));

    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postInvalidFood() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 100, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"));

        DietRequestDTO dietRequestDTO = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.bloodSugar").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.eatTime").value("Lunch"));

        String foodUrl = "http://localhost:" + port + "api/diary/food";
        StringBuilder foodName = new StringBuilder();
        IntStream.range(0, 100).forEach(i -> foodName.append("a"));
        FoodRequestDTO foodRequestDTO = new FoodRequestDTO(1L, 1L, 1L, foodName.toString());

        //when and then
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    @WithMockUser(roles = "Admin")
    public void postFood() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.Admin);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"));

        String year = "2021";
        String month = "09";
        String day = "25";
        String hour = "06";
        String minute = "49";
        String second = "41";
        DiabetesDiaryRequestDTO diaryRequestDTO = new DiabetesDiaryRequestDTO(1L, 100, "TEST", year, month, day, hour, minute, second);

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"));

        DietRequestDTO dietRequestDTO = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.bloodSugar").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.eatTime").value("Lunch"));

        String foodUrl = "http://localhost:" + port + "api/diary/food";
        FoodRequestDTO foodRequestDTO = new FoodRequestDTO(1L, 1L, 1L, "pizza");

        //when and then
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO)))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodName").value("pizza"));
    }

}