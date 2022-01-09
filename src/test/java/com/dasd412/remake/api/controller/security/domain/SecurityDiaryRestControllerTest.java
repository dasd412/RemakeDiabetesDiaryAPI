package com.dasd412.remake.api.controller.security.domain;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public class SecurityDiaryRestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private WriterRepository writerRepository;

    @Autowired
    private DiaryRepository diaryRepository;

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
    @WithMockUser(roles = "User")
    public void methodAccessTest_PostDiaryWithSecurity() throws Exception {
        //given
        String url = "/api/diary/user/diabetes-diary";

        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(1, 3).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i))
                .collect(Collectors.toList());

        SecurityDiaryPostRequestDTO dto = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .breakFastSugar(110).lunchSugar(120).dinnerSugar(130)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        //when and then
        mockMvc.perform(post(url).secure(true)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

}