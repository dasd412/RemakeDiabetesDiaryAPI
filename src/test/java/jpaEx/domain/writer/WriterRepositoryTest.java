package jpaEx.domain.writer;

import jpaEx.domain.diary.DiabetesDiary;
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
public class WriterRepositoryTest {
    @Autowired
    WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean(){
        writerRepository.deleteAll();
    }

    @Transactional
    @Test
    public void saveWriter(){

        //given
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
    }

    @Transactional
    @Test
    public void saveWriterWithDiary(){

        //given
        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        me.addDiary(diary);
        writerRepository.save(me);


        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());

        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
        logger.info(found.getDiaries().get(0).toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaryWithDiet(){

        //given

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Diet diet=new Diet(EatTime.Lunch,100,diary);
        diary.addDiet(diet);

        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        me.addDiary(diary);

        writerRepository.save(me);

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        //diary
        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
        logger.info(found.getDiaries().get(0).toString());

        //diet
        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaryWithDietWithFood(){
        //given

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Diet diet=new Diet(EatTime.Lunch,100,diary);
        diary.addDiet(diet);

        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        me.addDiary(diary);

        Food food=new Food("pizza",diet,me);
        diet.addFood(food);
        writerRepository.save(me);

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        //diary
        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
        logger.info(found.getDiaries().get(0).toString());

        //diet
        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());

        //food
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0)).isEqualTo(food);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(food.getFoodName());
        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).toString());
    }
}