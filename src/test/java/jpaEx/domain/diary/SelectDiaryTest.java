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
