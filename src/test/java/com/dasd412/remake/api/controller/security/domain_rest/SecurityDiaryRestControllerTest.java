package com.dasd412.remake.api.controller.security.domain_rest;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryUpdateDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodDTO;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityFoodForUpdateDTO;
import com.dasd412.remake.api.controller.security.domain_view.dto.PostForUpdateDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.service.domain.FindDiaryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.data.Percentage;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    private DietRepository dietRepository;

    @Autowired
    private FoodRepository foodRepository;

    @Autowired
    private FindDiaryService findDiaryService;

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
    public void postDiaryInvalidSizeWithSecurity() throws Exception {
        //given
        String url = "/api/diary/user/diabetes-diary";

        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(0, 5).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i*1000))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(0, 6).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i*1000))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(0, 7).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i*1000))
                .collect(Collectors.toList());

        SecurityDiaryPostRequestDTO dto = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(0).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .breakFastSugar(0).lunchSugar(0).dinnerSugar(0)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        //when and then
        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andDo(print());

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

    @Test
    public void deleteDiary() throws Exception {
        //일지뿐만 아니라 연관된 식단, 음식도 전부 삭제하는 것이 목적이다.

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

        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1));

        DiabetesDiary found = diaryRepository.findAll().get(0);


        //when and then
        String deleteUrl = "/api/diary/user/diabetes-diary/" + found.getId();
        mockMvc.perform(delete(deleteUrl).with(user(principalDetails)))
                .andDo(print());

        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        assertThat(diaries.size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);

    }

    @Test
    public void updateDiary() throws Exception {
        //given
        String url = "/api/diary/user/diabetes-diary";

        List<SecurityFoodDTO> breakFast = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("breakFast" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> lunch = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("lunch" + i, i))
                .collect(Collectors.toList());
        List<SecurityFoodDTO> dinner = IntStream.rangeClosed(1, 1).mapToObj(i -> new SecurityFoodDTO("dinner" + i, i))
                .collect(Collectors.toList());

        SecurityDiaryPostRequestDTO dto = SecurityDiaryPostRequestDTO.builder().fastingPlasmaGlucose(100).remark("test")
                .year("2021").month("12").day("22").hour("00").minute("00").second("00")
                .breakFastSugar(110).lunchSugar(120).dinnerSugar(130)
                .breakFastFoods(breakFast).lunchFoods(lunch).dinnerFoods(dinner).build();

        mockMvc.perform(post(url).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value("true"))
                .andExpect(jsonPath("$.response.id").value(1));

        DiabetesDiary targetDiary = findDiaryService.getDiabetesDiaryOfWriterWithRelation(EntityId.of(Writer.class, principalDetails.getWriter().getId()), EntityId.of(DiabetesDiary.class, 1L));
        PostForUpdateDTO viewDTO = new PostForUpdateDTO(targetDiary);

        List<SecurityFoodForUpdateDTO> oldBreakFast = viewDTO.getBreakFastFoods()
                .stream().map(elem -> new SecurityFoodForUpdateDTO(elem.getId(), breakFast.get(0).getFoodName(), breakFast.get(0).getAmount()))
                .collect(Collectors.toList());

        List<SecurityFoodForUpdateDTO> oldLunch = viewDTO.getLunchFoods()
                .stream().map(elem -> new SecurityFoodForUpdateDTO(elem.getId(), lunch.get(0).getFoodName(), lunch.get(0).getAmount()))
                .collect(Collectors.toList());

        List<SecurityFoodForUpdateDTO> oldDinner = viewDTO.getDinnerFoods()
                .stream().map(elem -> new SecurityFoodForUpdateDTO(elem.getId(), dinner.get(0).getFoodName(), dinner.get(0).getAmount()))
                .collect(Collectors.toList());

        List<SecurityFoodDTO> newBreakFast = viewDTO.getBreakFastFoods()
                .stream().map(elem -> new SecurityFoodDTO("pizza", 100.0))
                .collect(Collectors.toList());

        List<SecurityFoodDTO> newLunch = viewDTO.getLunchFoods()
                .stream().map(elem -> new SecurityFoodDTO("chicken", 200.0))
                .collect(Collectors.toList());

        List<SecurityFoodDTO> newDinner = viewDTO.getDinnerFoods()
                .stream().map(elem -> new SecurityFoodDTO("kimchi", 50.0))
                .collect(Collectors.toList());


        SecurityDiaryUpdateDTO updateDTO = SecurityDiaryUpdateDTO.builder()
                .diaryId(viewDTO.getDiaryId()).fastingPlasmaGlucose(200).remark("modify").diaryDirty(true)
                .breakFastId(viewDTO.getBreakFastId()).breakFastSugar(210).breakFastDirty(true)
                .lunchId(viewDTO.getLunchId()).lunchSugar(220).lunchDirty(true)
                .dinnerId(viewDTO.getDinnerId()).dinnerSugar(230).dinnerDirty(true)
                .oldBreakFastFoods(oldBreakFast).newBreakFastFoods(newBreakFast)
                .oldLunchFoods(oldLunch).newLunchFoods(newLunch)
                .oldDinnerFoods(oldDinner).newDinnerFoods(newDinner).build();


        //when
        String updateURL = "/api/diary/user/diabetes-diary";
        mockMvc.perform(put(updateURL).with(user(principalDetails))
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(new ObjectMapper().writeValueAsString(updateDTO)))
                .andDo(print());

        //then
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        assertThat(diaries.size()).isEqualTo(1);
        assertThat(diaries.get(0).getId()).isEqualTo(viewDTO.getDiaryId());
        assertThat(diaries.get(0).getFastingPlasmaGlucose()).isEqualTo(200);
        assertThat(diaries.get(0).getRemark()).isEqualTo("modify");

        assertThat(dietList.size()).isEqualTo(3);
        for (Diet diet : dietList) {
            switch (diet.getEatTime()) {
                case BreakFast:
                    assertThat(diet.getDietId()).isEqualTo(viewDTO.getBreakFastId());
                    assertThat(diet.getBloodSugar()).isEqualTo(210);
                    break;
                case Lunch:
                    assertThat(diet.getDietId()).isEqualTo(viewDTO.getLunchId());
                    assertThat(diet.getBloodSugar()).isEqualTo(220);
                    break;
                case Dinner:
                    assertThat(diet.getDietId()).isEqualTo(viewDTO.getDinnerId());
                    assertThat(diet.getBloodSugar()).isEqualTo(230);
                    break;
            }
        }

        assertThat(foodList.size()).isEqualTo(3);

        assertThat(foodList.get(0).getFoodName()).isEqualTo("pizza");
        assertThat(foodList.get(0).getAmount()).isCloseTo(100.0, Percentage.withPercentage(0.05));
        assertThat(foodList.get(1).getFoodName()).isEqualTo("chicken");
        assertThat(foodList.get(1).getAmount()).isCloseTo(200.0, Percentage.withPercentage(0.05));
        assertThat(foodList.get(2).getFoodName()).isEqualTo("kimchi");
        assertThat(foodList.get(2).getAmount()).isCloseTo(50.0, Percentage.withPercentage(0.05));
    }

}