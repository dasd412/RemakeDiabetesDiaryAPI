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
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);
        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        me.addDiary(diary);

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
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Diet diet=new Diet(EatTime.Lunch,100,diary);
        diary.addDiet(diet);
        me.addDiary(diary);

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
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        Diet diet=new Diet(EatTime.Lunch,100,diary);
        diary.addDiet(diet);
        me.addDiary(diary);


        Food food=new Food("pizza",diet);
        diet.addFood(food);


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

    @Transactional
    @Test
    public void modifyDiary(){
        //given
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        me.addDiary(diary);

        diary.modifyFastingPlasmaGlucose(45);
        diary.modifyRemark("modify");

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.getDiaries().get(0).toString());

        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(45);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("modify");
        logger.info(found.getDiaries().get(0).toString());
    }

    @Transactional
    @Test
    public void modifyDiet(){
        //given
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        me.addDiary(diary);

        Diet diet1=new Diet(EatTime.BreakFast,100);
        Diet diet2=new Diet(EatTime.Lunch,200);
        diary.addDiet(diet1);
        diary.addDiet(diet2);

        diet2.modifyBloodSugar(150);
        diet2.modifyEatTime(EatTime.Dinner);

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet1.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet1.getBloodSugar());
        assertThat(found.getDiaries().get(0).getDietList().get(1).getEatTime()).isEqualTo(EatTime.Dinner);
        assertThat(found.getDiaries().get(0).getDietList().get(1).getBloodSugar()).isEqualTo(150);
        logger.info(found.getDiaries().get(0).getDietList().toString());
    }

    @Transactional
    @Test
    public void modifyFood(){
        //given
        Writer me=new Writer("ME","TEST@NAVER.COM",Role.User);
        writerRepository.save(me);

        DiabetesDiary diary=new DiabetesDiary(20,"test", LocalDateTime.now());
        me.addDiary(diary);

        Diet diet1=new Diet(EatTime.BreakFast,100);
        Diet diet2=new Diet(EatTime.Lunch,200);
        diary.addDiet(diet1);
        diary.addDiet(diet2);

        Food food1=new Food("pizza",diet2);
        Food food2=new Food("cola",diet2);
        diet2.addFood(food1);
        diet2.addFood(food2);

        Food food3=new Food("tofu",diet1);
        diet1.addFood(food3);

        food2.setDiet(diet1);
        food2.setFoodName("water");

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(food3.getFoodName());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(1).getFoodName()).isEqualTo(food2.getFoodName());
        assertThat(found.getDiaries().get(0).getDietList().get(1).getFoodList().get(0).getFoodName()).isEqualTo(food1.getFoodName());
        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().toString());
        logger.info(found.getDiaries().get(0).getDietList().get(1).getFoodList().toString());
    }
    @Transactional
    @Test
    public void deleteDiary() {
        //given
        Writer me = new Writer("ME", "TEST@NAVER.COM", Role.User);
        writerRepository.save(me);

        DiabetesDiary diary1 = new DiabetesDiary(70, "test1", LocalDateTime.now());
        me.addDiary(diary1);

        DiabetesDiary diary2 = new DiabetesDiary(90, "test2", LocalDateTime.now());
        me.addDiary(diary2);

        DiabetesDiary diary3 = new DiabetesDiary(40, "test3", LocalDateTime.now());
        me.addDiary(diary3);

        me.getDiaries().remove(diary1);

        //when
        Writer found=writerRepository.findAll().get(0);

        //then
        assertThat(found.getDiaries().size()).isEqualTo(2);
        logger.info(found.getDiaries().toString());

    }

    @Transactional
    @Test
    public void deleteDiet() {
        //given
        Writer me = new Writer("ME", "TEST@NAVER.COM", Role.User);
        writerRepository.save(me);

        DiabetesDiary diary = new DiabetesDiary(70, "test", LocalDateTime.now());
        me.addDiary(diary);

        Diet diet1=new Diet(EatTime.BreakFast, 100);
        Diet diet2=new Diet(EatTime.Lunch, 200);
        Diet diet3=new Diet(EatTime.Dinner,100);
        diary.addDiet(diet1);
        diary.addDiet(diet2);
        diary.addDiet(diet3);

        Food tofu=new Food("tofu",diet1);
        Food pizza=new Food("pizza",diet2);
        Food cola=new Food("cola",diet2);
        Food chicken=new Food("chicken",diet2);
        Food soup=new Food("soup",diet3);
        diet1.addFood(tofu);
        diet2.addFood(pizza);
        diet2.addFood(cola);
        diet2.addFood(chicken);
        diet3.addFood(soup);

        diary.getDietList().remove(diet2);

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(found.getDiaries().get(0).getDietList().size()).isEqualTo(2);
        logger.info(found.getDiaries().get(0).getDietList().toString());
    }

    @Transactional
    @Test
    public void deleteFood() {
        //given
        Writer me = new Writer("ME", "TEST@NAVER.COM", Role.User);
        writerRepository.save(me);

        DiabetesDiary diary = new DiabetesDiary(70, "test", LocalDateTime.now());
        me.addDiary(diary);

        Diet diet1 = new Diet(EatTime.BreakFast, 100);
        Diet diet2 = new Diet(EatTime.Lunch, 200);
        Diet diet3 = new Diet(EatTime.Dinner, 100);
        diary.addDiet(diet1);
        diary.addDiet(diet2);
        diary.addDiet(diet3);

        Food tofu = new Food("tofu", diet1);
        Food pizza = new Food("pizza", diet2);
        Food cola = new Food("cola", diet2);
        Food chicken = new Food("chicken", diet2);
        Food soup = new Food("soup", diet3);
        diet1.addFood(tofu);
        diet2.addFood(pizza);
        diet2.addFood(cola);
        diet2.addFood(chicken);
        diet3.addFood(soup);

        diet2.getFoodList().remove(chicken);
        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(found.getDiaries().get(0).getDietList().get(1).getFoodList().size()).isEqualTo(2);
        logger.info(found.getDiaries().get(0).getDietList().get(1).getFoodList().toString());

    }

}