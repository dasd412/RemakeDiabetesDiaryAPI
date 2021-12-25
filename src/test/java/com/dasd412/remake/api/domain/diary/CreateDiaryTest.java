package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.service.SaveDiaryService;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
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

import javax.persistence.NoResultException;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class CreateDiaryTest {

    /*
    2021 - 12 -19 기준
    클래스 커버리지 100% (18/18)
    메소드 커버리지 56% (73/130)
    라인 커버리지 49% (163/332)
     */
    @Autowired
    SaveDiaryService saveDiaryService;

    @Autowired
    WriterRepository writerRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

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
    public void saveDiaryOneOfWriterOneById() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());

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
    public void saveDiariesOfWriterById() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test4", LocalDateTime.now());

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
    public void saveDiariesOfWritersById() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());

        Writer other = saveDiaryService.saveWriter("other", "OTHER@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 40, "test4", LocalDateTime.now());

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
    public void saveWriterWithDiaryWithDietOneById() {

        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 100);

        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));

        //then
        logger.info(found.toString());
        logger.info(found.getDiaries().get(0).toString());
        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());
        //writer
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());

        //diary
        assertThat(found.getDiaries().size()).isEqualTo(1);
        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());


        //diet
        assertThat(found.getDiaries().get(0).getDietList().size()).isEqualTo(1);
        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet);
        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());

    }

    @Transactional
    @Test
    public void saveWriterWithDiaryWithDietMany() {

        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Dinner, 150);

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 100);
        Food food = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

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
    public void saveWriterNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        Writer me = saveDiaryService.saveWriter("", "TEST@NAVER.COM", Role.User);
    }

    @Transactional
    @Test
    public void saveDiaryZeroBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 1 and 1000");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 0, "", LocalDateTime.now());
    }

    @Transactional
    @Test
    public void saveDiaryNegativeBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 1 and 1000");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), -1, "", LocalDateTime.now());
    }

    @Transactional
    @Test
    public void saveDietZeroBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 1 and 1000");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 0);
    }

    @Transactional
    @Test
    public void saveDietNegativeBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 1 and 1000");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, -200);
    }

    @Transactional
    @Test
    public void saveFoodNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        Writer me = saveDiaryService.saveWriter("me", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 150);
        Food food = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "");
    }
}