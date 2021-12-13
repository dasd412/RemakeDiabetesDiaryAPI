package jpaEx.domain.diary;

import jpaEx.domain.diet.Diet;
import jpaEx.domain.diet.EatTime;
import jpaEx.domain.food.Food;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class SelectDiaryTest {

    @Autowired
    DiabetesDiaryRepository diabetesDiaryRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before
    public void setUp(){
        LocalDateTime start=LocalDateTime.of(2021,11,1,0,0,0);
        IntStream.range(0,30).forEach(i->{


            Diet diet1=new Diet(EatTime.BreakFast,i+100);
            Diet diet2=new Diet(EatTime.Lunch, i+110);
            Diet diet3=new Diet(EatTime.Dinner,i+120);

            Food food1=new Food("a"+i,diet1);
            Food food2=new Food("b"+i,diet2);
            Food food3=new Food("c"+i,diet3);

            diet1.addFood(food1);
            diet2.addFood(food2);
            diet3.addFood(food3);

            DiabetesDiary diary=new DiabetesDiary(i+90,"test"+i,start.plusDays(i));
            diary.addDiet(diet1);
            diary.addDiet(diet2);
            diary.addDiet(diet3);

            diabetesDiaryRepository.save(diary);
        });
    }

    @After
    public void clean(){
        diabetesDiaryRepository.deleteAll();
    }

    @Transactional
    @Test
    public void testFindDiaryBetweenTime(){
        //given
        LocalDateTime startDate=LocalDateTime.of(2021,11,11,0,0,0);
        LocalDateTime endDate=LocalDateTime.of(2021,11,21,0,0,0);
        //when
        List<DiabetesDiary>diaries=diabetesDiaryRepository.findDiaryBetweenTime(startDate,endDate);

        //then
        int a=0;
        for(DiabetesDiary diary : diaries){
            //11은 시작일을 뜻함.
            assertThat(diary.getFastingPlasmaGlucose()).isEqualTo(a+100);
            assertThat(diary.getWrittenTime()).isBetween(startDate,endDate);
            logger.info(diary.toString());

            assertThat(diary.getDietList().get(0).getEatTime()).isEqualTo(EatTime.BreakFast);
            assertThat(diary.getDietList().get(0).getBloodSugar()).isEqualTo(a+100+11-1);
            assertThat(diary.getDietList().get(1).getEatTime()).isEqualTo(EatTime.Lunch);
            assertThat(diary.getDietList().get(1).getBloodSugar()).isEqualTo(a+110+11-1);
            assertThat(diary.getDietList().get(2).getEatTime()).isEqualTo(EatTime.Dinner);
            assertThat(diary.getDietList().get(2).getBloodSugar()).isEqualTo(a+120+11-1);

            logger.info(diary.getDietList().toString());

            assertThat(diary.getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo("a"+ (a + 11 - 1));
            assertThat(diary.getDietList().get(1).getFoodList().get(0).getFoodName()).isEqualTo("b"+ (a + 11 - 1));
            assertThat(diary.getDietList().get(2).getFoodList().get(0).getFoodName()).isEqualTo("c"+ (a + 11 - 1));

            a++;
        }
    }

    @Transactional
    @Test
    public void testFindDiaryHigherThan(){

    }

    @Transactional
    @Test
    public void testFindDietInDiary(){

    }

}
