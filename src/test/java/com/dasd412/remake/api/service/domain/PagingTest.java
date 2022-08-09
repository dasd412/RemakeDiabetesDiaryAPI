package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.controller.security.domain_view.FoodPageVO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
public class PagingTest {

    @Autowired
    private FindDiaryService findDiaryService;

    @Autowired
    private WriterRepository writerRepository;

    Writer me;

    @Before
    public void setUp() {
        //given
        me = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("test")
                .email("test@test.com")
                .build();

        DiabetesDiary diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), me, 110, "", LocalDateTime.of(2022, 11, 23, 2, 5));
        me.addDiary(diary);

        Diet diet1 = new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.BreakFast, 50);
        diary.addDiet(diet1);

        Diet diet2 = new Diet(EntityId.of(Diet.class, 2L), diary, EatTime.Lunch, 100);
        diary.addDiet(diet2);

        Diet diet3 = new Diet(EntityId.of(Diet.class, 3L), diary, EatTime.Dinner, 150);
        diary.addDiet(diet3);

        Diet diet4 = new Diet(EntityId.of(Diet.class, 4L), diary, EatTime.BreakFast, 200);
        diary.addDiet(diet4);

        LongStream.range(0, 50)
                .forEach(i -> {
                    Food food = new Food(EntityId.of(Food.class, i), diet1, "test1", i * 1.0);
                    diet1.addFood(food);
                });

        LongStream.range(0, 50)
                .forEach(i -> {
                    Food food = new Food(EntityId.of(Food.class, i + 50), diet2, "test2", (i + 50) * 1.0);
                    diet2.addFood(food);
                });

        LongStream.range(0, 50)
                .forEach(i -> {
                    Food food = new Food(EntityId.of(Food.class, i + 100), diet3, "test3", (i + 100) * 1.0);
                    diet3.addFood(food);
                });

        LongStream.range(0, 50)
                .forEach(i -> {
                    Food food = new Food(EntityId.of(Food.class, i + 150), diet4, "test4", (i + 150) * 1.0);
                    diet4.addFood(food);
                });

        writerRepository.save(me);
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    public void isPagingDoneWithoutPredicate() {
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), new FoodPageVO());
        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }

    @Test
    public void testPagingWithPredicateOfGreater() {
        FoodPageVO vo = FoodPageVO.builder().bloodSugar(100).sign("greater").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(10);
    }

    @Test
    public void testPagingWithPredicateOfLesser() {
        FoodPageVO vo = FoodPageVO.builder().bloodSugar(100).sign("lesser").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(5);
    }

    @Test
    public void testPagingWithPredicateOfEqual() {
        FoodPageVO vo = FoodPageVO.builder().bloodSugar(100).sign("equal").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(5);
    }

    @Test
    public void testPagingWithPredicateOfGreaterOrEqual() {
        FoodPageVO vo = FoodPageVO.builder().bloodSugar(100).sign("ge").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(15);
    }

    @Test
    public void testPagingWithPredicateOfLesserOrEqual() {
        FoodPageVO vo = FoodPageVO.builder().bloodSugar(200).sign("le").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }

    @Test
    public void testPagingWithPredicateOfInvalidDateFormat() {
        FoodPageVO vo = FoodPageVO.builder().startYear("test").startMonth("ww").startDay("adsad").build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }

    @Test
    public void testPagingWithPredicateOfInvalidDateOrder() {
        FoodPageVO vo = FoodPageVO.builder()
                .startYear("2022").startMonth("11").startDay("25")
                .endYear("2022").endMonth("11").endDay("1")
                .build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }

    @Test
    public void testPagingWithPredicateOfBetweenDate() {
        FoodPageVO vo = FoodPageVO.builder()
                .startYear("2022").startMonth("11").startDay("1")
                .endYear("2022").endMonth("11").endDay("30")
                .build();
        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);
        assertThat(dtoPage.getTotalPages()).isEqualTo(20);
    }

}