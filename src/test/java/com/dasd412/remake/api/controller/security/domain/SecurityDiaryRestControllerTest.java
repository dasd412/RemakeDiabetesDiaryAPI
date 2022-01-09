package com.dasd412.remake.api.controller.security.domain;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain.dto.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain.dto.SecurityFoodDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.NoResultException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private SaveDiaryService saveDiaryService;

    private final TestUserDetailsService testUserDetailsService = new TestUserDetailsService();

    private PrincipalDetails principalDetails;

    private MockMvc mockMvc;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setup() {
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
        logger.info("set end \n");
    }

    @After
    public void clean() {
        logger.info("clean\n");
        writerRepository.deleteAll();
    }

    @Test
    public void postDiaryHasNullWithSecurity() throws Exception {
        //given
        String url = "/api/diary/user/diabetes-diary";

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
        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1));
        logger.info("repository \n");
        DiabetesDiary found = diaryRepository.findDiabetesDiaryOfWriterWithRelation(1L, 1L).orElseThrow(NoResultException::new);
        List<Food> foodList = foodRepository.findAll();

        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(0);
        assertThat(found.getRemark()).isEqualTo("test");
        assertThat(found.getDietList().size()).isEqualTo(3);
        assertThat(foodList.size()).isEqualTo(breakFast.size() + lunch.size() + dinner.size());

    }

    @Test
    public void postDiaryWithSecurity() throws Exception {
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
        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1));
        logger.info("repository \n");
        DiabetesDiary found = diaryRepository.findDiabetesDiaryOfWriterWithRelation(1L, 1L).orElseThrow(NoResultException::new);
        List<Food> foodList = foodRepository.findAll();

        assertThat(found.getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(found.getRemark()).isEqualTo("test");
        assertThat(found.getDietList().size()).isEqualTo(3);
        assertThat(foodList.size()).isEqualTo(breakFast.size() + lunch.size() + dinner.size());

    }

}