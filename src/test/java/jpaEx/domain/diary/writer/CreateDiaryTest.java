package jpaEx.domain.diary.writer;

import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diet.Diet;
import jpaEx.domain.diary.diet.EatTime;
import jpaEx.domain.diary.food.Food;
import jpaEx.service.SaveDiaryService;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
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
public class CreateDiaryTest {

    @Autowired
    SaveDiaryService saveDiaryService;

    @Autowired
    WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    //예외 캐치용 객체
    @Rule
    public ExpectedException thrown=ExpectedException.none();

    @After
    public void clean() {
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
    }

    /*
    Save 테스트
     */
    @Transactional
    @Test
    public void saveWriterOne() {

        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
    }

    @Transactional
    @Test
    public void saveWritersMany() {

        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);

        Writer other = saveDiaryService.saveWriter("other", "OTHER@NAVER.COM", Role.User);

        Writer another = saveDiaryService.saveWriter("another", "Another@NAVER.COM", Role.User);

        //when
        Writer foundMe = writerRepository.findAll().get(0);
        Writer foundOther = writerRepository.findAll().get(1);
        Writer foundAnother = writerRepository.findAll().get(2);

        //then
        assertThat(foundMe).isEqualTo(me);
        assertThat(foundMe.getName()).isEqualTo(me.getName());
        assertThat(foundMe.getEmail()).isEqualTo(me.getEmail());
        assertThat(foundMe.getRole()).isEqualTo(me.getRole());
        logger.info(foundMe.toString());

        assertThat(foundOther).isEqualTo(other);
        assertThat(foundOther.getName()).isEqualTo(other.getName());
        assertThat(foundOther.getEmail()).isEqualTo(other.getEmail());
        assertThat(foundOther.getRole()).isEqualTo(other.getRole());
        logger.info(foundOther.toString());

        assertThat(foundAnother).isEqualTo(another);
        assertThat(foundAnother.getName()).isEqualTo(another.getName());
        assertThat(foundAnother.getEmail()).isEqualTo(another.getEmail());
        assertThat(foundAnother.getRole()).isEqualTo(another.getRole());
        logger.info(foundAnother.toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaryOne() {

        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(20);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("test");
        logger.info(found.getDiaries().get(0).toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaries() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 40, "test4", LocalDateTime.now());
        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(10);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("test1");
        logger.info(found.getDiaries().get(0).toString());

        assertThat(found.getDiaries().get(1).getFastingPlasmaGlucose()).isEqualTo(20);
        assertThat(found.getDiaries().get(1).getRemark()).isEqualTo("test2");
        logger.info(found.getDiaries().get(1).toString());

        assertThat(found.getDiaries().get(2).getFastingPlasmaGlucose()).isEqualTo(30);
        assertThat(found.getDiaries().get(2).getRemark()).isEqualTo("test3");
        logger.info(found.getDiaries().get(2).toString());

        assertThat(found.getDiaries().get(3).getFastingPlasmaGlucose()).isEqualTo(40);
        assertThat(found.getDiaries().get(3).getRemark()).isEqualTo("test4");
        logger.info(found.getDiaries().get(3).toString());
    }

    @Transactional
    @Test
    public void saveWritersWithDiaries() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());

        Writer other = saveDiaryService.saveWriter("other", "OTHER@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(other, 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiary(other, 40, "test4", LocalDateTime.now());

        //when
        Writer foundMe = writerRepository.findAll().get(0);
        Writer foundOther = writerRepository.findAll().get(1);

        //then
        assertThat(foundMe).isEqualTo(me);
        assertThat(foundMe.getName()).isEqualTo(me.getName());
        assertThat(foundMe.getEmail()).isEqualTo(me.getEmail());
        assertThat(foundMe.getRole()).isEqualTo(me.getRole());
        logger.info(foundMe.toString());

        assertThat(foundMe.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(10);
        assertThat(foundMe.getDiaries().get(0).getRemark()).isEqualTo("test1");
        logger.info(foundMe.getDiaries().get(0).toString());

        assertThat(foundMe.getDiaries().get(1).getFastingPlasmaGlucose()).isEqualTo(20);
        assertThat(foundMe.getDiaries().get(1).getRemark()).isEqualTo("test2");
        logger.info(foundMe.getDiaries().get(1).toString());

        assertThat(foundOther).isEqualTo(other);
        assertThat(foundOther.getName()).isEqualTo(other.getName());
        assertThat(foundOther.getEmail()).isEqualTo(other.getEmail());
        assertThat(foundOther.getRole()).isEqualTo(other.getRole());
        logger.info(foundOther.toString());

        assertThat(foundOther.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(30);
        assertThat(foundOther.getDiaries().get(0).getRemark()).isEqualTo("test3");
        logger.info(foundOther.getDiaries().get(0).toString());

        assertThat(foundOther.getDiaries().get(1).getFastingPlasmaGlucose()).isEqualTo(40);
        assertThat(foundOther.getDiaries().get(1).getRemark()).isEqualTo("test4");
        logger.info(foundOther.getDiaries().get(1).toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaryWithDietOne() {

        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 100);

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());
        logger.info(found.getDiaries().toString());

        //diary
        assertThat(me.getDiaries().size()).isEqualTo(1);
        assertThat(found.getDiaries().size()).isEqualTo(1);
        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
        logger.info(found.getDiaries().get(0).toString());

        //diet
        assertThat(me.getDiaries().get(0).getDietList().size()).isEqualTo(1);
        assertThat(found.getDiaries().get(0).getDietList().size()).isEqualTo(1);
        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());
    }

    @Transactional
    @Test
    public void saveWriterWithDiaryWithDietMany() {

        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDiet(me, diary, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDiet(me, diary, EatTime.Dinner, 150);

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        //diary
        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
        logger.info(found.getDiaries().get(0).toString());

        //diet1
        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet1);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet1.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet1.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());

        //diet2
        assertThat(found.getDiaries().get(0).getDietList().get(1)).isEqualTo(diet2);
        assertThat(found.getDiaries().get(0).getDietList().get(1).getEatTime()).isEqualTo(diet2.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(1).getBloodSugar()).isEqualTo(diet2.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(1).toString());

        //diet3
        assertThat(found.getDiaries().get(0).getDietList().get(2)).isEqualTo(diet3);
        assertThat(found.getDiaries().get(0).getDietList().get(2).getEatTime()).isEqualTo(diet3.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(2).getBloodSugar()).isEqualTo(diet3.getBloodSugar());
        logger.info(found.getDiaries().get(0).getDietList().get(2).toString());
    }


    @Transactional
    @Test
    public void saveWriterWithDiaryWithDietWithFoodOne() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 100);
        Food food = saveDiaryService.saveFood(me, diet, "pizza");

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

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
    public void saveWriterWithDiaryWithDietWithFoodMany() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFood(me, diet, "pizza");
        Food food2 = saveDiaryService.saveFood(me, diet, "chicken");
        Food food3 = saveDiaryService.saveFood(me, diet, "cola");

        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

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

        //food1
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0)).isEqualTo(food1);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(food1.getFoodName());
        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).toString());

        //food2
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(1)).isEqualTo(food2);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(1).getFoodName()).isEqualTo(food2.getFoodName());
        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(1).toString());

        //food2
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(2)).isEqualTo(food3);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(2).getFoodName()).isEqualTo(food3.getFoodName());
        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(2).toString());
    }

    /*
    예외 캐치 테스트
     */
    @Transactional
    @Test
    public void saveWriterNoName(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        Writer me = saveDiaryService.saveWriter("", "TEST@NAVER.COM", Role.User);
    }

    @Transactional
    @Test
    public void saveDiaryZeroBloodSugar(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be positive number");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary=saveDiaryService.saveDiary(me,0,"",LocalDateTime.now());
    }

    @Transactional
    @Test
    public void saveDiaryNegativeBloodSugar(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be positive number");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary=saveDiaryService.saveDiary(me,-1,"",LocalDateTime.now());
    }

    @Transactional
    @Test
    public void saveDietZeroBloodSugar(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be positive number");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary=saveDiaryService.saveDiary(me,100,"",LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 0);
    }

    @Transactional
    @Test
    public void saveDietNegativeBloodSugar(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be positive number");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary=saveDiaryService.saveDiary(me,100,"",LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, -200);
    }

    @Transactional
    @Test
    public void saveFoodNoName(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary=saveDiaryService.saveDiary(me,100,"",LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 150);
        Food food = saveDiaryService.saveFood(me, diet, "");
    }
}