package com.dasd412.remake.api.controller.diary;


import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryRequestDTO;
import com.dasd412.remake.api.controller.diary.diabetesdiary.DiaryUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodUpdateRequestDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class DiaryUpdateDeleteRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    WriterRepository writerRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();

        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.User);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

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
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO)))
                .andExpect(status().isOk());

        String foodUrl = "http://localhost:" + port + "api/diary/food";
        FoodRequestDTO foodRequestDTO = new FoodRequestDTO(1L, 1L, 1L, "pizza");

        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO)))
                .andExpect(status().isOk());
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }
    
    @Test
    public void updateInvalidDiary() throws Exception {
        //given
        long invalidDiaryId = 2L;
        DiaryUpdateRequestDTO dto = new DiaryUpdateRequestDTO(1L, invalidDiaryId, 200, "modify");
        String url = "http://localhost:" + port + "api/diary/diabetes-diary";

        //when
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    
    @Test
    public void updateDiaryInvalidSugar() throws Exception {
        //given
        DiaryUpdateRequestDTO dto = new DiaryUpdateRequestDTO(1L, 1L, 20000, "modify");
        String url = "http://localhost:" + port + "api/diary/diabetes-diary";

        //when
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    public void updateDiaryInvalidRemark() throws Exception {
        //given
        StringBuilder invalidRemark = new StringBuilder();
        IntStream.range(0, 1000).forEach(i -> invalidRemark.append("a"));

        DiaryUpdateRequestDTO dto = new DiaryUpdateRequestDTO(1L, 1L, 300, invalidRemark.toString());
        String url = "http://localhost:" + port + "api/diary/diabetes-diary";

        //when
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    public void updateDiary() throws Exception {
        //given

        DiaryUpdateRequestDTO dto = new DiaryUpdateRequestDTO(1L, 1L, 300, "modifyTest");
        String url = "http://localhost:" + port + "api/diary/diabetes-diary";

        //when and then
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.fastingPlasmaGlucose").value("300"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.remark").value("modifyTest"));

        DiabetesDiary foundDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(1L, 1L).orElseThrow(() -> new NoSuchElementException("일지 없음."));
        assertThat(foundDiary.getFastingPlasmaGlucose()).isEqualTo(300);
        assertThat(foundDiary.getRemark()).isEqualTo("modifyTest");
    }

    
    @Test
    public void deleteDiary() throws Exception {
        //given
        long writerId = 1L;
        long diaryId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId;

        //when and then
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"));

        //cascade test
        List<DiabetesDiary> diaryList = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        assertThat(diaryList.size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    
    @Test
    public void updateDietInvalidSugar() throws Exception {
        //given
        int invalidSugar = 0;
        DietUpdateRequestDTO dto = new DietUpdateRequestDTO(1L, 1L, 1L, EatTime.BreakFast, invalidSugar);
        String url = "http://localhost:" + port + "api/diary/diet";

        //when and then
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    public void updateDiet() throws Exception {
        //given
        DietUpdateRequestDTO dto = new DietUpdateRequestDTO(1L, 1L, 1L, EatTime.BreakFast, 100);
        String url = "http://localhost:" + port + "api/diary/diet";

        //when and then
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.eatTime").value("BreakFast"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.bloodSugar").value("100"));

        Diet diet = dietRepository.findOneDietByIdInDiary(1L, 1L, 1L).orElseThrow(() -> new NoSuchElementException("식단 없음."));
        assertThat(diet.getBloodSugar()).isEqualTo(100);
        assertThat(diet.getEatTime()).isEqualTo(EatTime.BreakFast);
    }

    
    @Test
    public void deleteDiet() throws Exception {
        //given
        long writerId = 1L;
        long diaryId = 1L;
        long dietId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId + "/diet/" + dietId;

        //when and then
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"));

        //cascade test
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        assertThat(dietList.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    
    @Test
    public void updateFoodInvalidName() throws Exception {
        //given
        StringBuilder invalidName = new StringBuilder();
        IntStream.range(0, 100).forEach(i -> invalidName.append("a"));

        FoodUpdateRequestDTO dto = new FoodUpdateRequestDTO(1L, 1L, 1L, invalidName.toString());

        String url = "http://localhost:" + port + "api/diary/food";

        //when and then
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    
    @Test
    public void updateFood() throws Exception {
        FoodUpdateRequestDTO dto = new FoodUpdateRequestDTO(1L, 1L, 1L, "chicken");

        String url = "http://localhost:" + port + "api/diary/food";

        //when and then
        mockMvc.perform(put(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodName").value("chicken"));
    }

    
    @Test
    public void deleteFood() throws Exception {
        //given
        long writerId = 1L;
        long diaryId = 1L;
        long dietId = 1L;
        long foodId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId + "/diet/" + dietId + "/food/" + foodId;

        //when and then
        mockMvc.perform(delete(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodId").value("1"));

        List<Food> foodList = foodRepository.findAll();
        assertThat(foodList.size()).isEqualTo(0);
    }

}