package jpaEx.domain.writer;

import jpaEx.domain.EntityId;
import jpaEx.domain.diary.DiabetesDiary;
import jpaEx.domain.diary.DiaryRepository;
import jpaEx.domain.diet.Diet;
import jpaEx.domain.diet.DietRepository;
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
public class CRUDWriterTest {
    @Autowired
    WriterRepository writerRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DietRepository dietRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean() {
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
    }

    //작성자 id 생성 메서드 todo 실제 사용할 때 트랜잭션 처리 필수다.
    // 파라미터에 리포지토리를 넣은 이유는, 파라미터가 아무 것도 없을 경우 혹시라도 getId를 다른 것을 호출할 수 있기 때문에 넣었다.
    public EntityId<Writer> getIdOfWriter(WriterRepository writerRepository) {
        Long count = writerRepository.findCountOfId();
        Long writerId;
        if (count == 0) {
            writerId = 0L;
        } else {
            writerId = writerRepository.findMaxOfId();
        }
        return new EntityId<>(writerId+1);
    }

    //일지 id 생성 메서드 (트랜잭션 필수)
    public EntityId<DiabetesDiary> getIdOfDiary(DiaryRepository diaryRepository) {
        Long count = diaryRepository.findCountOfId();
        Long diaryId;
        if (count == 0) {
            diaryId = 0L;
        } else {
            diaryId = diaryRepository.findMaxOfId();
        }
        return new EntityId<>(diaryId + 1);
    }

    //식단 id 생성 메서드 (트랜잭션 필수)
    public EntityId<Diet> getIdOfDiet(DietRepository dietRepository) {
        Long count = dietRepository.findCountOfId();
        Long dietId;
        if (count == 0) {
            dietId = 0L;
        } else {
            dietId = dietRepository.findMaxOfId();
        }
        return new EntityId<>(dietId + 1);
    }

    //나중에 서비스 레이어에 쓸 예정. getIdOfWriter()의 경우 트랜잭션 처리 안하면 다른 스레드가 껴들어 올 경우 id 값이 중복될 수 있어 기본키 조건을 위배할 수도 있다.
    @Transactional
    public Writer saveWriter(String name, String email, Role role) {
        Writer writer = new Writer(getIdOfWriter(writerRepository), name, email, role);
        writerRepository.save(writer);
        return writer;
    }

    @Transactional
    public DiabetesDiary saveDiary(Writer writer, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        DiabetesDiary diary = new DiabetesDiary(getIdOfDiary(diaryRepository), writer, fastingPlasmaGlucose, remark, writtenTime);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diary;
    }

    @Transactional
    public Diet saveDiet(Writer writer, DiabetesDiary diary, EatTime eatTime, int bloodSugar) {
        Diet diet = new Diet(getIdOfDiet(dietRepository), diary, eatTime, bloodSugar);
        diary.addDiet(diet);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diet;
    }


    @Transactional
    @Test
    public void countAndMaxOfIdWhenEmpty() {
        //given
        Long count = writerRepository.findCountOfId();
        logger.info("count : " + count);
        assertThat(count).isEqualTo(0L);

        Long maxId = writerRepository.findMaxOfId();
        logger.info("maxId : " + maxId);
        assertThat(maxId).isNull();

    }


    @Transactional
    @Test
    public void saveWriterOne() {

        //given
        Writer me = saveWriter("ME", "TEST@NAVER.COM", Role.User);

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
        Writer me = saveWriter("me", "ME@NAVER.COM", Role.User);

        Writer other = saveWriter("other", "OTHER@NAVER.COM", Role.User);

        Writer another = saveWriter("another", "Another@NAVER.COM", Role.User);

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
        Writer me = saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiary(me, 20, "test", LocalDateTime.now());

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
        Writer me = saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiary(me, 20, "test2", LocalDateTime.now());
        saveDiary(me, 30, "test3", LocalDateTime.now());
        saveDiary(me, 40, "test4", LocalDateTime.now());
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
        Writer me = saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiary(me, 20, "test2", LocalDateTime.now());

        Writer other = saveWriter("other", "OTHER@NAVER.COM", Role.User);
        saveDiary(other, 30, "test3", LocalDateTime.now());
        saveDiary(other, 40, "test4", LocalDateTime.now());

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
    public void saveWriterWithDiaryWithDiet() {

        //given
        Writer me = saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiet(me, diary, EatTime.Lunch, 100);

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
    }

//    //todo
//    @Transactional
//    @Test
//    public void saveWriterWithDiaryWithDietWithFood() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 20, "test", LocalDateTime.now());
//        Diet diet = new Diet(1L, diary, EatTime.Lunch, 100);
//        diary.addDiet(diet);
//        me.addDiary(diary);
//
//        Food food = new Food(1L, diet, "pizza");
//        diet.addFood(food);
//
//        writerRepository.save(me);
//
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        //writer
//        assertThat(found).isEqualTo(me);
//        assertThat(found.getName()).isEqualTo(me.getName());
//        assertThat(found.getEmail()).isEqualTo(me.getEmail());
//        assertThat(found.getRole()).isEqualTo(me.getRole());
//        //diary
//        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
//        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(diary.getFastingPlasmaGlucose());
//        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo(diary.getRemark());
//        logger.info(found.getDiaries().get(0).toString());
//
//        //diet
//        assertThat(found.getDiaries().get(0).getDietList().get(0)).isEqualTo(diet);
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet.getEatTime());
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet.getBloodSugar());
//        logger.info(found.getDiaries().get(0).getDietList().get(0).toString());
//
//        //food
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0)).isEqualTo(food);
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(food.getFoodName());
//        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).toString());
//    }
//
//    @Transactional
//    @Test
//    public void modifyDiary() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 20, "test", LocalDateTime.now());
//        me.addDiary(diary);
//        writerRepository.save(me);
//
//        diary.modifyFastingPlasmaGlucose(45);
//        diary.modifyRemark("modify");
//        writerRepository.save(me);
//
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found).isEqualTo(me);
//        assertThat(found.getName()).isEqualTo(me.getName());
//        assertThat(found.getEmail()).isEqualTo(me.getEmail());
//        assertThat(found.getRole()).isEqualTo(me.getRole());
//        logger.info(found.getDiaries().get(0).toString());
//
//        assertThat(found.getDiaries().get(0)).isEqualTo(diary);
//        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(45);
//        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("modify");
//        logger.info(found.getDiaries().get(0).toString());
//    }
//
//    @Transactional
//    @Test
//    public void modifyDiet() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 20, "test", LocalDateTime.now());
//        me.addDiary(diary);
//
//        Diet diet1 = new Diet(1L, diary, EatTime.BreakFast, 100);
//        Diet diet2 = new Diet(2L, diary, EatTime.Lunch, 200);
//        diary.addDiet(diet1);
//        diary.addDiet(diet2);
//        writerRepository.save(me);
//
//        diet2.modifyBloodSugar(150);
//        diet2.modifyEatTime(EatTime.Dinner);
//        writerRepository.save(me);
//
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getEatTime()).isEqualTo(diet1.getEatTime());
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getBloodSugar()).isEqualTo(diet1.getBloodSugar());
//        assertThat(found.getDiaries().get(0).getDietList().get(1).getEatTime()).isEqualTo(EatTime.Dinner);
//        assertThat(found.getDiaries().get(0).getDietList().get(1).getBloodSugar()).isEqualTo(150);
//        logger.info(found.getDiaries().get(0).getDietList().toString());
//    }
//
//    @Transactional
//    @Test
//    public void modifyFood() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 20, "test", LocalDateTime.now());
//        me.addDiary(diary);
//
//        Diet diet1 = new Diet(1L, diary, EatTime.BreakFast, 100);
//        Diet diet2 = new Diet(2L, diary, EatTime.Lunch, 200);
//        diary.addDiet(diet1);
//        diary.addDiet(diet2);
//
//        Food food1 = new Food(1L, diet2, "pizza");
//        Food food2 = new Food(2L, diet2, "cola");
//        diet2.addFood(food1);
//        diet2.addFood(food2);
//        Food food3 = new Food(3L, diet1, "tofu");
//        diet1.addFood(food3);
//        writerRepository.save(me);
//
//        food2.modifyFoodName("water");
//        writerRepository.save(me);
//
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().get(0).getFoodName()).isEqualTo(food3.getFoodName());
//        assertThat(found.getDiaries().get(0).getDietList().get(1).getFoodList().get(1).getFoodName()).isEqualTo(food2.getFoodName());
//        assertThat(found.getDiaries().get(0).getDietList().get(1).getFoodList().get(0).getFoodName()).isEqualTo(food1.getFoodName());
//        logger.info(found.getDiaries().get(0).getDietList().get(0).getFoodList().toString());
//        logger.info(found.getDiaries().get(0).getDietList().get(1).getFoodList().toString());
//    }
//
//    @Transactional
//    @Test
//    public void deleteDiary() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//
//        DiabetesDiary diary1 = new DiabetesDiary(1L, me, 70, "test1", LocalDateTime.now());
//        me.addDiary(diary1);
//
//        DiabetesDiary diary2 = new DiabetesDiary(2L, me, 90, "test2", LocalDateTime.now());
//        me.addDiary(diary2);
//
//        DiabetesDiary diary3 = new DiabetesDiary(3L, me, 40, "test3", LocalDateTime.now());
//        me.addDiary(diary3);
//        writerRepository.save(me);
//
//        me.getDiaries().remove(diary1);
//        writerRepository.save(me);
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found.getDiaries().size()).isEqualTo(2);
//        logger.info(found.getDiaries().toString());
//
//    }
//
//    @Transactional
//    @Test
//    public void deleteDiet() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 70, "test", LocalDateTime.now());
//        me.addDiary(diary);
//
//        Diet diet1 = new Diet(1L, diary, EatTime.BreakFast, 100);
//        Diet diet2 = new Diet(2L, diary, EatTime.Lunch, 200);
//        Diet diet3 = new Diet(3L, diary, EatTime.Dinner, 100);
//        diary.addDiet(diet1);
//        diary.addDiet(diet2);
//        diary.addDiet(diet3);
//
//        Food tofu = new Food(1L, diet1, "tofu");
//        Food pizza = new Food(2L, diet2, "pizza");
//        Food cola = new Food(3L, diet2, "cola");
//        Food chicken = new Food(4L, diet2, "chicken");
//        Food soup = new Food(5L, diet3, "soup");
//        diet1.addFood(tofu);
//        diet2.addFood(pizza);
//        diet2.addFood(cola);
//        diet2.addFood(chicken);
//        diet3.addFood(soup);
//        writerRepository.save(me);
//
//        diary.getDietList().remove(diet2);
//        writerRepository.save(me);
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found.getDiaries().get(0).getDietList().size()).isEqualTo(2);
//        logger.info(found.getDiaries().get(0).getDietList().toString());
//    }
//
//    @Transactional
//    @Test
//    public void deleteFood() {
//        //given
//        Writer me = new Writer(1L, "ME", "TEST@NAVER.COM", Role.User);
//
//        DiabetesDiary diary = new DiabetesDiary(1L, me, 70, "test", LocalDateTime.now());
//        me.addDiary(diary);
//
//        Diet diet1 = new Diet(1L, diary, EatTime.BreakFast, 100);
//        Diet diet2 = new Diet(2L, diary, EatTime.Lunch, 200);
//        Diet diet3 = new Diet(3L, diary, EatTime.Dinner, 100);
//        diary.addDiet(diet1);
//        diary.addDiet(diet2);
//        diary.addDiet(diet3);
//
//        Food tofu = new Food(1L, diet1, "tofu");
//        Food pizza = new Food(2L, diet2, "pizza");
//        Food cola = new Food(3L, diet2, "cola");
//        Food chicken = new Food(4L, diet2, "chicken");
//        Food soup = new Food(5L, diet3, "soup");
//        diet1.addFood(tofu);
//        diet2.addFood(pizza);
//        diet2.addFood(cola);
//        diet2.addFood(chicken);
//        diet3.addFood(soup);
//        writerRepository.save(me);
//
//        diet2.getFoodList().remove(chicken);
//        writerRepository.save(me);
//        //when
//        Writer found = writerRepository.findAll().get(0);
//
//        //then
//        assertThat(found.getDiaries().get(0).getDietList().get(1).getFoodList().size()).isEqualTo(2);
//        logger.info(found.getDiaries().get(0).getDietList().get(1).getFoodList().toString());
//
//    }

}