package com.dasd412.remake.api.controller.diary;

import com.dasd412.remake.api.controller.diary.diabetesdiary.DiabetesDiaryRequestDTO;
import com.dasd412.remake.api.controller.diary.diet.DietRequestDTO;
import com.dasd412.remake.api.controller.diary.food.FoodRequestDTO;
import com.dasd412.remake.api.controller.diary.writer.WriterJoinRequestDTO;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.writer.Role;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class DiaryFindRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    hasSize()는 json array 의 길이를 판단하는 메서드다.
     */

    @Before
    public void setup() throws Exception {
        logger.info("set up start");
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
        logger.info("set up end");
    }

    @After
    public void clean() {
        logger.info("clean up");
        writerRepository.deleteAll();
    }

    @Test
    public void findInvalidOwnerOfDiary() throws Exception {
        //given
        long invalidId = 2L;
        String url = "http://localhost:" + port + "api/diary/owner/diabetesDiary/" + invalidId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    public void findOwnerOfDiary() throws Exception {
        //given
        long validId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/diabetes-diary/" + validId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.email").value("test@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value("User"));
    }


    @Test
    public void findDiabetesDiariesOfWriter() throws Exception {
        //given
        DiabetesDiaryRequestDTO diaryRequestDTO1 = new DiabetesDiaryRequestDTO(1L, 200, "TEST@", "2021", "09", "26", "00", "00", "00");

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO1)))
                .andExpect(status().isOk());

        DiabetesDiaryRequestDTO diaryRequestDTO2 = new DiabetesDiaryRequestDTO(1L, 150, "TEST@@", "2021", "09", "27", "00", "00", "00");

        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/list";

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }


    @Test
    public void findDiaryOfInvalidOwner() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test2", "test2@naver.com", Role.User);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk()).andDo(print());

        long writerId = 2L;
        long id = 1L;
        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetesDiary/" + id;

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    public void findInvalidDiaryOfOwner() throws Exception {
        //given
        long writerId = 1L;
        long invalidId = 2L;
        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetesDiary/" + invalidId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isNotFound());
    }


    @Test
    public void findDiaryOfOwner() throws Exception {
        //given
        long writerId = 1L;
        long invalidId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + invalidId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.fastingPlasmaGlucose").value("100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.remark").value("TEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writtenTime").value("2021-09-25T06:49:41"));
    }

    @Test
    public void findOneDiabetesDiaryOfOwnerWithRelation() throws Exception {
        //given
        long writerId = 1L;
        long diaryId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId + "/with/relations";

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietList[0].dietId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietList[0].eatTime").value("Lunch"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.dietList[0].bloodSugar").value("200"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodList[0].foodId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodList[0].foodName").value("pizza"));

    }

    @Test
    public void findDiariesBetweenTimeInvalidFormat() throws Exception {
        //given
        String startDate = "2021-09-25-06:49:41";
        String endDate = "2021-09-26:06:49:41";
        long writerId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findDiariesBetweenTimeInvalidTimeOrder() throws Exception {
        //given
        String startDate = "2021-09-25T06:49:41";
        String endDate = "2021-09-22T06:49:41";
        long writerId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findDiariesBetweenTime() throws Exception {
        //given
        DiabetesDiaryRequestDTO diaryRequestDTO1 = new DiabetesDiaryRequestDTO(1L, 200, "TEST@", "2021", "09", "26", "00", "00", "00");

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO1)))
                .andExpect(status().isOk());

        DiabetesDiaryRequestDTO diaryRequestDTO2 = new DiabetesDiaryRequestDTO(1L, 150, "TEST@@", "2021", "09", "27", "00", "00", "00");

        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;

        String startDate = "2021-09-25T00:00:00";
        String endDate = "2021-09-27T23:59:59";

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/start-date/" + startDate + "/end-date/" + endDate;

        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }


    @Test
    public void findFpgHigherOrEqual() throws Exception {
        //given
        DiabetesDiaryRequestDTO diaryRequestDTO1 = new DiabetesDiaryRequestDTO(1L, 200, "TEST@", "2021", "09", "26", "00", "00", "00");

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO1)))
                .andExpect(status().isOk());

        DiabetesDiaryRequestDTO diaryRequestDTO2 = new DiabetesDiaryRequestDTO(1L, 150, "TEST@@", "2021", "09", "27", "00", "00", "00");

        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int fastingPlasmaGlucose = 150;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/ge/" + fastingPlasmaGlucose;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(2)));
    }


    @Test
    public void findFpgLowerOrEqual() throws Exception {
        //given
        DiabetesDiaryRequestDTO diaryRequestDTO1 = new DiabetesDiaryRequestDTO(1L, 200, "TEST@", "2021", "09", "26", "00", "00", "00");

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetes-diary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO1)))
                .andExpect(status().isOk());

        DiabetesDiaryRequestDTO diaryRequestDTO2 = new DiabetesDiaryRequestDTO(1L, 150, "TEST@@", "2021", "09", "27", "00", "00", "00");

        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int fastingPlasmaGlucose = 200;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/le/" + fastingPlasmaGlucose;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }


    @Test
    public void findDietsOfDiaryInvalidWriter() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long invalidWriterId = 2L;
        long diaryId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + invalidWriterId + "/diabetes-diary/" + diaryId + "/diet/list";

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(0)));
    }

    /*
    식단 조회 테스트
     */

    @Test
    public void findDietsOfInvalidDiary() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        long invalidDiaryId = 2L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + invalidDiaryId + "/diet/list";

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(0)));
    }


    @Test
    public void findDietsOfDiary() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 200);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        long diaryId = 1L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId + "/diet/list";

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }


    @Test
    public void findOneDietOfDiary() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Dinner, 300);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.BreakFast, 150);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        long diaryId = 1L;
        long dietId = 3L;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetes-diary/" + diaryId + "/diet/" + dietId;
        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.eatTime").value("BreakFast"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.bloodSugar").value(150));
    }


    @Test
    public void findHigherThanBloodSugarBetweenTimeInvalidTimeOrder() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Dinner, 300);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.BreakFast, 150);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String startDate = "2021-09-25T06:49:41";
        String endDate = "2021-09-22T06:49:41";
        int bloodSugar = 100;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + bloodSugar + "/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findHigherThanInvalidBloodSugarBetweenTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Dinner, 300);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.BreakFast, 150);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String startDate = "2021-09-25T00:00:00";
        String endDate = "2021-09-27T23:59:59";
        int bloodSugar = -100;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + bloodSugar + "/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findHigherThanBloodSugarBetweenTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Dinner, 300);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.BreakFast, 150);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String startDate = "2021-09-25T00:00:00";
        String endDate = "2021-09-27T23:59:59";
        int bloodSugar = 100;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + bloodSugar + "/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }


    @Test
    public void findLowerThanBloodSugarBetweenTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Dinner, 300);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.BreakFast, 150);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String startDate = "2021-09-25T00:00:00";
        String endDate = "2021-09-27T23:59:59";
        int bloodSugar = 100;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/le/" + bloodSugar + "/start-date/" + startDate + "/end-date/" + endDate;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(0)));
    }


    @Test
    public void findHigherThanInvalidBloodSugarInEatTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 100);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 300);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int invalidBloodSugar = -200;
        EatTime eatTime = EatTime.Lunch;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + invalidBloodSugar + "/eat-time/" + eatTime;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findHigherThanBloodSugarInEatTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 100);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 300);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int invalidBloodSugar = 200;
        EatTime eatTime = EatTime.Lunch;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + invalidBloodSugar + "/eat-time/" + eatTime;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(2)));
    }


    @Test
    public void findLowerThanBloodSugarInEatTime() throws Exception {
        //given
        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 100);
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 300);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int invalidBloodSugar = 150;
        EatTime eatTime = EatTime.Lunch;

        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/le/" + invalidBloodSugar + "/eat-time/" + eatTime;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(1)));
    }


    @Test
    public void findAverageBloodSugarOfDietInvalid() throws Exception {
        //식단 기록한 적 없는데 식단의 평균 혈당 요청한 경우 테스트
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test@@@", "test123@naver.com", Role.User);
        String url = "http://localhost:" + port + "/api/diary/writer";

        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());

        long writerIdWithoutDiet = 2L;

        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerIdWithoutDiet + "/diet/average";

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void findAverageBloodSugarOfDiet() throws Exception {

        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 100);

        String postDietUrl = "http://localhost:" + port + "api/diary/diet";

        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 300);

        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;

        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/average";

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(closeTo(200.0, 0.005)));
    }

    /*
    음식 조회 테스트
     */

    @Test
    public void findFoodsInDiet() throws Exception {
        //given
        String foodUrl = "http://localhost:" + port + "api/diary/food";
        FoodRequestDTO foodRequestDTO = new FoodRequestDTO(1L, 1L, 1L, "cola");

        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO)))
                .andExpect(status().isOk());

        long writerId = 1L;
        long dietId = 1L;
        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/" + dietId + "/food/list";

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(2)));
    }


    @Test
    public void findOneFoodByIdInDiet() throws Exception {
        //given
        long writerId = 1L;
        long dietId = 1L;
        long foodId = 1L;
        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/" + dietId + "/food/" + foodId;

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.foodName").value("pizza"));
    }


    @Test
    public void findFoodNamesInDietHigherThanBloodSugar() throws Exception {
        //given
        String foodUrl = "http://localhost:" + port + "api/diary/food";
        FoodRequestDTO foodRequestDTO1 = new FoodRequestDTO(1L, 1L, 1L, "cola");

        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO1)))
                .andExpect(status().isOk());

        FoodRequestDTO foodRequestDTO2 = new FoodRequestDTO(1L, 1L, 1L, "chicken");

        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        int bloodSugar = 100;
        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/" + bloodSugar + "/food/names";

        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasItems("pizza", "chicken", "cola")));

    }


    @Test
    public void findFoodHigherThanAverageBloodSugarOfDiet() throws Exception {
        //given
        String postDietUrl = "http://localhost:" + port + "api/diary/diet";
        String foodUrl = "http://localhost:" + port + "api/diary/food";

        DietRequestDTO dietRequestDTO1 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 210);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO1)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO2 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 220); //<-평균 혈당
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO2)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO3 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 230);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO3)))
                .andExpect(status().isOk());

        DietRequestDTO dietRequestDTO4 = new DietRequestDTO(1L, 1L, EatTime.Lunch, 240);
        mockMvc.perform(post(postDietUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dietRequestDTO4)))
                .andExpect(status().isOk());

        FoodRequestDTO foodRequestDTO1 = new FoodRequestDTO(1L, 1L, 2L, "ham");
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO1)))
                .andExpect(status().isOk());

        FoodRequestDTO foodRequestDTO2 = new FoodRequestDTO(1L, 1L, 3L, "chicken"); //<-
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO2)))
                .andExpect(status().isOk());

        FoodRequestDTO foodRequestDTO3 = new FoodRequestDTO(1L, 1L, 4L, "egg"); //<-
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO3)))
                .andExpect(status().isOk());

        FoodRequestDTO foodRequestDTO4 = new FoodRequestDTO(1L, 1L, 5L, "sprite"); //<-
        mockMvc.perform(post(foodUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(foodRequestDTO4)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String targetUrl = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diet/ge/average/food/names";

        //when and then
        mockMvc.perform(get(targetUrl).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasItems("chicken", "egg", "sprite")));
    }

}