package jpaEx.domain.diary;

import jpaEx.domain.diet.Diet;

import jpaEx.domain.diet.EatTime;
import jpaEx.domain.food.Food;
import org.junit.After;
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

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class DiabetesDiaryRepositoryTest {

    @Autowired
    DiabetesDiaryRepository diabetesDiaryRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean(){
        diabetesDiaryRepository.deleteAll();
    }

    @Transactional
    @Test
    public void saveDiary(){
        //given
        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        diabetesDiaryRepository.save(diary);
        //when
        DiabetesDiary found=diabetesDiaryRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(diary);
    }

    @Transactional
    @Test
    public void saveDiaryWithDiet(){
        //given
        Diet diet=new Diet(EatTime.BreakFast,100);
        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        diary.addDiet(diet);

        diabetesDiaryRepository.save(diary);

        //when
        DiabetesDiary found=diabetesDiaryRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(diary);
        assertThat(found.getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(found.getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());
        logger.info(found.getDietList().get(0).toString());
    }

    @Transactional
    @Test
    public void saveDiaryWithDietWhichHasFood() {
        //given
        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Diet diet=new Diet(EatTime.Lunch,100,diary);
        Food pizza=new Food("pizza",diet);
        diet.addFood(pizza);
        diary.addDiet(diet);

        diabetesDiaryRepository.save(diary);
        //when
        DiabetesDiary foundDiary=diabetesDiaryRepository.findAll().get(0);

        //then
        //다이어리 맞는 지 체크
        assertThat(foundDiary).isEqualTo(diary);

        //다이어리 안에 식단 넣어졌는 지 체크
        assertThat(foundDiary.getDietList().get(0)).isEqualTo(diet);
        assertThat(foundDiary.getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(foundDiary.getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());

        //다이어리 안의 식단에 음식이 넣어졌는 지 체크
        assertThat(foundDiary.getDietList().get(0).getFoodList().get(0)).isEqualTo(pizza);
        assertThat(foundDiary.getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(pizza.getFoodName());
        logger.info(foundDiary.getDietList().get(0).getFoodList().get(0).toString());
    }

}