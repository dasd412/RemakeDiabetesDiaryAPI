package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.dasd412.remake.api.service.domain.UpdateDeleteDiaryService;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations="classpath:application-test.properties")
public class UpdateDeleteDiaryTest {
    /*
    2021 - 12 -19 기준
    클래스 커버리지 100% (18/18)
    메소드 커버리지 69% (90/130)
    라인 커버리지 61% (203/332)
     */
    @Autowired
    SaveDiaryService saveDiaryService;

    @Autowired
    UpdateDeleteDiaryService updateDeleteDiaryService;

    @Autowired
    WriterRepository writerRepository;

    @Autowired
    DiaryRepository diaryRepository;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean() {
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
    }

    @Transactional
    @Test
    public void updateDiary() {

        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());

        updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), 100, "modifyTest");

        //when
        Writer found = writerRepository.findAll().get(0);
        DiabetesDiary foundDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("modifyTest");
        logger.info(found.getDiaries().get(0).toString());

        assertThat(foundDiary.getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(foundDiary.getRemark()).isEqualTo("modifyTest");
        logger.info(foundDiary.toString());
    }

    @Transactional
    @Test
    public void deleteDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.getDiaries().toString());

        assertThat(found.getDiaries().size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteSomeDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 30, "test3", LocalDateTime.now());
        DiabetesDiary diary4 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test4", LocalDateTime.now());

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        assertThat(found.getDiaries().size()).isEqualTo(2);
        assertThat(diaries.size()).isEqualTo(2);

        logger.info(found.toString());
        logger.info(found.getDiaries().toString());
        logger.info(diaries.toString());
    }

    @Transactional
    @Test
    public void deleteWriterCascadeDiary() {
        //작성자 삭제되면 일지도 삭제되는 지 cascade 테스트
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test4", LocalDateTime.now());
        writerRepository.bulkDeleteWriter(me.getId());

        //when
        List<Writer> writers = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        assertThat(writers.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void updateDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);

        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        updateDeleteDiaryService.updateDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()), EatTime.Lunch, 200);

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet1.getDietId())
                .orElseThrow(() -> new NoResultException("식단 없음"));

        //then
        logger.info(diary.getDietList().toString());
        assertThat(diary.getDietList().contains(diet)).isTrue();

        logger.info(diet.toString());
        assertThat(diet.getBloodSugar()).isEqualTo(200);
        assertThat(diet.getEatTime()).isEqualTo(EatTime.Lunch);
    }

    @Transactional
    @Test
    public void deleteDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));
        List<Diet> dietList = dietRepository.findAll();

        //then
        logger.info(diary.getDietList().toString());
        assertThat(diary.getDietList().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteSomeDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);

        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet5.getDietId()));
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet6.getDietId()));


        //when
        DiabetesDiary foundDiary1 = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));
        DiabetesDiary foundDiary2 = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary2.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));
        List<Diet> dietList = dietRepository.findAll();

        //then
        logger.info(foundDiary1.getDietList().toString());
        assertThat(foundDiary1.getDietList().size()).isEqualTo(2);
        assertThat(foundDiary1.getDietList().contains(diet1)).isFalse();

        logger.info(foundDiary2.getDietList().toString());
        assertThat(foundDiary2.getDietList().size()).isEqualTo(1);
        assertThat(foundDiary2.getDietList().contains(diet5)).isFalse();
        assertThat(foundDiary2.getDietList().contains(diet6)).isFalse();

        logger.info(dietList.toString());
        assertThat(dietList.size()).isEqualTo(3);
    }

    @Transactional
    @Test
    public void deleteWriterCascadeDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        writerRepository.bulkDeleteWriter(me.getId());

        //when
        List<Writer> writers = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();

        //then
        assertThat(writers.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteDiaryCascadeDiet() {
        //given
        logger.info("save start");
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        logger.info("delete start");
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()));

        logger.info("delete end");
        //when
        List<Writer> found = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.get(0).getDiaries().toString());
        logger.info(dietList.toString());
        logger.info(diaries.toString());
        assertThat(found.get(0).getDiaries().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void updateFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        updateDeleteDiaryService.updateFood(EntityId.of(Writer.class, me.getId()), EntityId.of(Diet.class, diet1.getDietId()), EntityId.of(Food.class, food1.getId()), "chicken");

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary.getId(), diet1.getDietId())
                .orElseThrow(() -> new NoResultException("식단 없음"));

        Food food = foodRepository.findOneFoodByIdInDiet(me.getId(), diet.getDietId(), food1.getId())
                .orElseThrow(() -> new NoResultException("음식 없음"));

        //then
        logger.info(diet.getFoodList().toString());
        assertThat(diet.getFoodList().contains(food)).isTrue();
        assertThat(diet.getFoodList().get(0).getFoodName()).isEqualTo(food.getFoodName());

        logger.info(food.toString());
        assertThat(food.getFoodName()).isEqualTo("chicken");
    }

    @Transactional
    @Test
    public void deleteFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");

        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), EntityId.of(Food.class, food1.getId()));

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary.getId(), diet1.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));
        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(diet.getFoodList().toString());
        assertThat(diet.getFoodList().size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteSomeFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");
        Food food4 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "beer");

        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), EntityId.of(Food.class, food2.getId()));
        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), EntityId.of(Food.class, food4.getId()));

        //when
        Diet foundDiet = dietRepository.findOneDietByIdInDiary(me.getId(), diary.getId(), diet.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));
        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(foundDiet.getFoodList().toString());
        assertThat(foundDiet.getFoodList().size()).isEqualTo(2);
        assertThat(foundDiet.getFoodList().contains(food1)).isTrue();
        assertThat(foundDiet.getFoodList().contains(food3)).isTrue();

        logger.info(foodList.toString());
        assertThat(foodList.size()).isEqualTo(2);
        assertThat(foodList.contains(food2)).isFalse();
        assertThat(foodList.contains(food4)).isFalse();
    }

    @Transactional
    @Test
    public void deleteWriterCascadeFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        writerRepository.bulkDeleteWriter(me.getId());

        //when
        List<Writer> writers = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        //then
        assertThat(writers.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteDiaryCascadeFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()));

        //when
        List<Writer> found = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.get(0).getDiaries().toString());
        logger.info(dietList.toString());
        logger.info(diaries.toString());
        logger.info(foodList.toString());

        assertThat(found.get(0).getDiaries().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteDietCascadeFood() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()));

        //when
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(dietList.toString());
        logger.info(foodList.toString());

        assertThat(dietList.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    /*
    예외 캐치 테스트
     */
    @Transactional
    @Test
    public void updateDiaryBloodSugarInappropriate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 1 and 1000");

        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 200, "test", LocalDateTime.now());
        updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), -190, "modifyTest");
    }

    @Transactional
    @Test
    public void updateDietBloodSugarInappropriate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 1 and 1000");

        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        updateDeleteDiaryService.updateDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()), EatTime.Lunch, -1900);
    }

    @Transactional
    @Test
    public void updateFoodNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");

        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        updateDeleteDiaryService.updateFood(EntityId.of(Writer.class, me.getId()), EntityId.of(Diet.class, diet1.getDietId()), EntityId.of(Food.class, food1.getId()), "");
    }

}
