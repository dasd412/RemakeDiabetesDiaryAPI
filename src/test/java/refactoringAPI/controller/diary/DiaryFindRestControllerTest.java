package refactoringAPI.controller.diary;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import refactoringAPI.controller.diary.diabetesdiary.DiabetesDiaryRequestDTO;
import refactoringAPI.controller.diary.diet.DietRequestDTO;
import refactoringAPI.controller.diary.food.FoodRequestDTO;
import refactoringAPI.controller.diary.writer.WriterJoinRequestDTO;
import refactoringAPI.domain.diary.diet.EatTime;
import refactoringAPI.domain.diary.writer.Role;

import static org.hamcrest.Matchers.hasSize;
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

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /*
    hasSize()는 json array 의 길이를 판단하는 메서드다.
     */

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

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetesDiary";
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

    @Transactional
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

    @Transactional
    @Test
    public void findOwnerOfDiary() throws Exception {
        //given
        long validId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/diabetesDiary/" + validId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.name").value("test"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.email").value("test@naver.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value("User"));
    }

    @Transactional
    @Test
    public void findDiabetesDiariesOfWriter() throws Exception {
        //given
        DiabetesDiaryRequestDTO diaryRequestDTO1 = new DiabetesDiaryRequestDTO(1L, 200, "TEST@", "2021", "09", "26", "00", "00", "00");

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetesDiary";
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO1)))
                .andExpect(status().isOk());

        DiabetesDiaryRequestDTO diaryRequestDTO2 = new DiabetesDiaryRequestDTO(1L, 150, "TEST@@", "2021", "09", "27", "00", "00", "00");

        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO2)))
                .andExpect(status().isOk());

        long writerId = 1L;
        String url = "http://localhost:" + port + "api/diary/diabetesDiary/list/owner/" + writerId;

        //when and then
        mockMvc.perform(get(url).contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response").value(hasSize(3)));
    }

    @Transactional
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

    @Transactional
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

    @Transactional
    @Test
    public void findDiaryOfOwner() throws Exception {
        //given
        long writerId = 1L;
        long invalidId = 1L;
        String url = "http://localhost:" + port + "api/diary/owner/" + writerId + "/diabetesDiary/" + invalidId;

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

}