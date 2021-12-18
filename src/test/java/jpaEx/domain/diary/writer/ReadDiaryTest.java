package jpaEx.domain.diary.writer;

import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
import jpaEx.domain.diary.diet.Diet;
import jpaEx.domain.diary.diet.DietRepository;
import jpaEx.domain.diary.diet.EatTime;
import jpaEx.domain.diary.food.Food;
import jpaEx.domain.diary.food.FoodRepository;
import jpaEx.service.SaveDiaryService;
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
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class ReadDiaryTest {

    @Autowired
    SaveDiaryService saveDiaryService;

    @Autowired
    WriterRepository writerRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean() {
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
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

    /*
    일지 조회
    */
    @Transactional
    @Test
    public void findByIdOfWriter() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);

        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoSuchElementException("해당 작성자가 존재하지 않습니다."));

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());
    }

    @Transactional
    @Test
    public void findWriterOfDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());

        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoSuchElementException("해당 작성자가 존재하지 않습니다."));
        Writer foundOfDiary = diaryRepository.findWriterOfDiary(diary.getId()).orElseThrow(() -> new NoSuchElementException("해당 작성자가 존재하지 않습니다."));
        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(foundOfDiary).isEqualTo(found);
        assertThat(foundOfDiary.getName()).isEqualTo(found.getName());
        assertThat(foundOfDiary.getEmail()).isEqualTo(found.getEmail());
        assertThat(foundOfDiary.getRole()).isEqualTo(found.getRole());
        logger.info(foundOfDiary.toString());
    }

    @Transactional
    @Test
    public void findDiariesOfWriter() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 10, "test2", LocalDateTime.now());

        //when
        List<DiabetesDiary> diaries = diaryRepository.findDiabetesDiariesOfWriter(me.getId());

        //then
        assertThat(diaries.get(0)).isEqualTo(diary1);
        assertThat(diaries.get(0).getWriter()).isEqualTo(me);
        assertThat(diaries.get(0).getFastingPlasmaGlucose()).isEqualTo(diary1.getFastingPlasmaGlucose());
        assertThat(diaries.get(0).getRemark()).isEqualTo(diary1.getRemark());

        assertThat(diaries.get(1)).isEqualTo(diary2);
        assertThat(diaries.get(1).getWriter()).isEqualTo(me);
        assertThat(diaries.get(1).getFastingPlasmaGlucose()).isEqualTo(diary2.getFastingPlasmaGlucose());
        assertThat(diaries.get(1).getRemark()).isEqualTo(diary2.getRemark());

    }

    @Transactional
    @Test
    public void findDiaryOfWriterByBothId() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 10, "test2", LocalDateTime.now());
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 40, "test3", LocalDateTime.now());

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary2.getId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 일지입니다."));

        //then
        assertThat(diary).isEqualTo(diary2);
        assertThat(diary.getWriter()).isEqualTo(me);
        assertThat(diary.getFastingPlasmaGlucose()).isEqualTo(diary2.getFastingPlasmaGlucose());
        assertThat(diary.getRemark()).isEqualTo(diary2.getRemark());
        logger.info(diary.toString());
    }

    @Transactional
    @Test
    public void findDiariesBetweenTime() {
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        //when
        List<DiabetesDiary> diaries = diaryRepository.findDiaryBetweenTime(me.getId(), LocalDateTime.of(2021, 12, 15, 0, 0, 0), LocalDateTime.of(2021, 12, 31, 0, 0, 0));

        //then
        assertThat(diaries.get(0)).isEqualTo(diary3);
        assertThat(diaries.get(0).getWriter()).isEqualTo(me);
        assertThat(diaries.get(0).getFastingPlasmaGlucose()).isEqualTo(diary3.getFastingPlasmaGlucose());
        assertThat(diaries.get(0).getRemark()).isEqualTo(diary3.getRemark());
        logger.info(diaries.get(0).toString());
    }

    @Transactional
    @Test
    public void findFpgHigherOrEqual() {
        //then
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 40, "test3", LocalDateTime.now());

        //when
        List<DiabetesDiary> diaries = diaryRepository.findFpgHigherOrEqual(me.getId(), 15);

        //then
        logger.info(diaries.toString());
        assertThat(diaries.size()).isEqualTo(2);
    }

    @Transactional
    @Test
    public void findFpgLowerOrEqual() {
        //then
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 40, "test3", LocalDateTime.now());

        //when
        List<DiabetesDiary> diaries = diaryRepository.findFpgLowerOrEqual(me.getId(), 15);

        //then
        logger.info(diaries.toString());
        assertThat(diaries.size()).isEqualTo(1);
    }

    /*
    식단 조회
     */
    @Transactional
    @Test
    public void findDietsOfDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDiet(me, diary, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDiet(me, diary, EatTime.Dinner, 150);

        //when
        List<Diet> dietList = dietRepository.findDietsInDiary(me.getId(), diary.getId());

        //then
        logger.info(dietList.toString());
        assertThat(dietList.size()).isEqualTo(3);
        assertThat(dietList.get(0).getEatTime()).isEqualTo(diet1.getEatTime());
        assertThat(dietList.get(0).getBloodSugar()).isEqualTo(diet1.getBloodSugar());

        assertThat(dietList.get(1).getEatTime()).isEqualTo(diet2.getEatTime());
        assertThat(dietList.get(1).getBloodSugar()).isEqualTo(diet2.getBloodSugar());

        assertThat(dietList.get(2).getEatTime()).isEqualTo(diet3.getEatTime());
        assertThat(dietList.get(2).getBloodSugar()).isEqualTo(diet3.getBloodSugar());

    }

    @Transactional
    @Test
    public void findDietOfDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDiet(me, diary, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDiet(me, diary, EatTime.Dinner, 150);

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary.getId(), diet3.getDietId())
                .orElseThrow(() -> new NoSuchElementException("해당 식단이 존재하지 않습니다."));

        //then
        logger.info(diet.toString());
        assertThat(diet.getEatTime()).isEqualTo(diet3.getEatTime());
        assertThat(diet.getBloodSugar()).isEqualTo(diet3.getBloodSugar());
    }

    @Transactional
    @Test
    public void findHigherThanBloodSugarBetweenTime() {
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDiet(me, diary2, EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDiet(me, diary2, EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDiet(me, diary2, EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDiet(me, diary3, EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDiet(me, diary3, EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDiet(me, diary3, EatTime.Dinner, 140);

        //when
        List<Diet> dietList = dietRepository.findHigherThanBloodSugarBetweenTime(me.getId(), 150, LocalDateTime.of(2021, 12, 5, 0, 0, 0), LocalDateTime.of(2021, 12, 27, 0, 0, 0));

        //then
        logger.info(dietList.toString());
        assertThat(dietList.size()).isEqualTo(3);
        assertThat(dietList.contains(diet5)).isTrue();
        assertThat(dietList.contains(diet6)).isTrue();
        assertThat(dietList.contains(diet7)).isTrue();
    }

    @Transactional
    @Test
    public void findLowerThanBloodSugarBetweenTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDiet(me, diary2, EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDiet(me, diary2, EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDiet(me, diary2, EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDiet(me, diary3, EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDiet(me, diary3, EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDiet(me, diary3, EatTime.Dinner, 140);

        //when
        List<Diet> dietList = dietRepository.findLowerThanBloodSugarBetweenTime(me.getId(), 150, LocalDateTime.of(2021, 12, 5, 0, 0, 0), LocalDateTime.of(2021, 12, 27, 0, 0, 0));

        //then
        logger.info(dietList.toString());
        assertThat(dietList.size()).isEqualTo(4);
        assertThat(dietList.contains(diet4)).isTrue();
        assertThat(dietList.contains(diet7)).isTrue();
        assertThat(dietList.contains(diet8)).isTrue();
        assertThat(dietList.contains(diet9)).isTrue();
    }

    @Transactional
    @Test
    public void findHigherThanBloodSugarInEatTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDiet(me, diary2, EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDiet(me, diary2, EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDiet(me, diary2, EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDiet(me, diary3, EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDiet(me, diary3, EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDiet(me, diary3, EatTime.Dinner, 140);

        //when
        List<Diet> dietList = dietRepository.findHigherThanBloodSugarInEatTime(me.getId(), 120, EatTime.Lunch);

        //then
        logger.info(dietList.toString());
        assertThat(dietList.size()).isEqualTo(2);
        assertThat(dietList.contains(diet5)).isTrue();
        assertThat(dietList.contains(diet8)).isTrue();
    }

    /*
    음식 조회
     */
    @Transactional
    @Test
    public void findFoodsInDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFood(me, diet, "pizza");
        Food food2 = saveDiaryService.saveFood(me, diet, "chicken");
        Food food3 = saveDiaryService.saveFood(me, diet, "cola");

        //when
        List<Food> foodList = foodRepository.findFoodsInDiet(me.getId(), diary.getId(), diet.getDietId());

        //then
        logger.info(foodList.toString());
        assertThat(foodList.size()).isEqualTo(3);
        assertThat(foodList.contains(food1)).isTrue();
        assertThat(foodList.contains(food2)).isTrue();
        assertThat(foodList.contains(food3)).isTrue();
    }

    @Transactional
    @Test
    public void findOneFoodByIdInDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFood(me, diet, "pizza");
        Food food2 = saveDiaryService.saveFood(me, diet, "chicken");
        Food food3 = saveDiaryService.saveFood(me, diet, "cola");

        //when
        Food foundFood = foodRepository.findOneFoodByIdInDiet(me.getId(), diary.getId(), diet.getDietId(), food3.getId())
                .orElseThrow(() -> new NoSuchElementException("음식 없음."));

        //then
        logger.info(foundFood.toString());
        assertThat(foundFood).isEqualTo(food3);
        assertThat(foundFood.getFoodName()).isEqualTo(food3.getFoodName());
        assertThat(foundFood.getDiet()).isEqualTo(food3.getDiet());
    }

    @Transactional
    @Test
    public void findFoodNamesInDietHigherThanBloodSugar() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFood(me, diet1, "pizza");
        Food food2 = saveDiaryService.saveFood(me, diet1, "chicken");
        Food food3 = saveDiaryService.saveFood(me, diet1, "cola");

        Diet diet2 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 200);
        Food food4 = saveDiaryService.saveFood(me, diet2, "ham");
        Food food5 = saveDiaryService.saveFood(me, diet2, "chicken");
        Food food6 = saveDiaryService.saveFood(me, diet2, "cola");

        Diet diet3 = saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 150);
        Food food7 = saveDiaryService.saveFood(me, diet3, "ham");
        Food food8 = saveDiaryService.saveFood(me, diet3, "egg");
        Food food9 = saveDiaryService.saveFood(me, diet3, "water");

        //when
        List<String> foodNames = foodRepository.findFoodNamesInDietHigherThanBloodSugar(me.getId(), 200);

        //then
        logger.info(foodNames.toString());
        assertThat(foodNames.size()).isEqualTo(4);
        assertThat(foodNames.contains("pizza")).isTrue();
        assertThat(foodNames.contains("chicken")).isTrue();
        assertThat(foodNames.contains("cola")).isTrue();
        assertThat(foodNames.contains("ham")).isTrue();
    }

    @Transactional
    @Test
    public void testNPlusOne() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());
        saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 200);
        saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 210);
        saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 220);
        saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 230);
        saveDiaryService.saveDiet(me, diary, EatTime.Lunch, 240);

        //when
        logger.info("find all test");
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        for (DiabetesDiary d : diaries) {
            logger.info(d.getDietList().toString());
        }

        //todo n+1문제는 안 일어나는 것 처럼 보인다. 하지만 왜 그럴까..?
    }
}
