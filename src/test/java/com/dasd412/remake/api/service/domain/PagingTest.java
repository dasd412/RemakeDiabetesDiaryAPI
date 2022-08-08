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
    private SaveDiaryService saveDiaryService;

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

        DiabetesDiary diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), me, 110, "", LocalDateTime.now());
        me.addDiary(diary);

        Diet diet = new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.Dinner, 100);
        diary.addDiet(diet);

        LongStream.range(0, 200)
                .forEach(i -> {
                    Food food = new Food(EntityId.of(Food.class, i), diet, String.valueOf(i), i * 1.0);
                    diet.addFood(food);
                });

        writerRepository.save(me);
    }

    @After
    public void clean() {
        writerRepository.deleteAll();
    }

    @Test
    public void getThirdPage() {
        FoodPageVO vo = new FoodPageVO();
        vo.setPage(3);
        vo.setSize(10);

        Page<FoodBoardDTO> dtoPage = findDiaryService.getFoodByPagination(EntityId.of(Writer.class, 1L), vo);

    }

}