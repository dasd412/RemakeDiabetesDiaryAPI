package refactoringAPI.controller.diary;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import refactoringAPI.controller.diary.writer.WriterJoinRequestDTO;
import refactoringAPI.domain.diary.writer.Role;

import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Profile("test")
public class DiarySaveRestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    //todo writer 는 스프링 시큐리티 적용 후 빼낼 예정
    @Transactional
    @Test
    public void joinWriterInvalidName() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("", "test@naver.com", Role.User);
        String url = "http://localhost:" + port + "/api/diary/writer";

        //when and then
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Transactional
    @Test
    public void joinWriter() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.User);
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.role").value("User"));
    }

    @Transactional
    @Test
    public void postDiaryInvalidFpg() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.User);
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

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetesDiary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void postDiaryInvalidRemark() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.User);
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

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetesDiary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Transactional
    @Test
    public void postDiary() throws Exception {
        //given
        WriterJoinRequestDTO dto = new WriterJoinRequestDTO("test", "test@naver.com", Role.User);
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

        String postDiaryUrl = "http://localhost:" + port + "api/diary/diabetesDiary";
        //when and then
        mockMvc.perform(post(postDiaryUrl).contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(diaryRequestDTO))).andDo(print())
                .andExpect(MockMvcResultMatchers.jsonPath("$.success").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.diaryId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writerId").value("1"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.fastingPlasmaGlucose").value("100"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.remark").value("TEST"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.writtenTime").value("2021-09-25T06:49:41"));
    }

}