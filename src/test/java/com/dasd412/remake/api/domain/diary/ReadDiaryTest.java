package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.service.SaveDiaryService;
import org.hibernate.Hibernate;
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
import org.assertj.core.data.Offset;
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

    /*
    2021 - 12 -19 기준
    클래스 커버리지 100% (18/18)
    메소드 커버리지 70% (91/130)
    라인 커버리지 71% (236/332)
     */

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

    // todo Writer 엔티티 하나를 조회할 때는 Writer 하나만 조회하도록 변경해야 한다. 연관된 엔티티 전부 select 하면 성능 상 매우 안 좋다.
    //n+1문제 발생. n+1 문제를 테스트하려면 @Transactional 을 제거해야 한다.
    @Test
    public void findWriterAllIsNotFetchJoin() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));
        //when
        Writer found = writerRepository.findAll().get(0);

        //then
        assertThat(Hibernate.isInitialized(found.getDiaries())).isFalse();
    }

    //n+1문제 발생. n+1 문제를 테스트하려면 @Transactional 을 제거해야 한다.
    @Test
    public void findWriterByIdIsNotFetchJoin() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));
        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoSuchElementException("작성자 없음."));

        //then
        assertThat(Hibernate.isInitialized(found.getDiaries())).isFalse();
    }

    // todo DiabetesDiary 엔티티 하나를 조회할 때는 연관된 엔티티 "전부"를 함께 조회해야 한다.
    //n+1문제 발생. n+1 문제를 테스트하려면 @Transactional 을 제거해야 한다.
    @Test
    public void findDiaryOfWriterIsFetchJoin() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "cola");

        //when
        DiabetesDiary foundDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary.getId()).orElseThrow(() -> new NoSuchElementException("존재하지 않는 일지입니다."));

        //then
        assertThat(Hibernate.isInitialized(foundDiary.getDietList())).isFalse();
        //assertThat(Hibernate.isInitialized(foundDiary.getDietList().get(0).getFoodList())).isFalse(); <- lazy loading 인데 초기화 아직 안되서 에러.
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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test2", LocalDateTime.now());

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test2", LocalDateTime.now());
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test3", LocalDateTime.now());

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

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
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test3", LocalDateTime.now());

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
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test3", LocalDateTime.now());

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Dinner, 150);

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Dinner, 150);

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

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
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        Diet diet9 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        //when
        List<Food> foodList = foodRepository.findFoodsInDiet(me.getId(), diet.getDietId());

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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        //when
        Food foundFood = foodRepository.findOneFoodByIdInDiet(me.getId(), diet.getDietId(), food3.getId())
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
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        Food food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        Food food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "cola");

        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Food food4 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "ham");
        Food food5 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "chicken");
        Food food6 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "cola");

        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 150);
        Food food7 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "ham");
        Food food8 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "egg");
        Food food9 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "water");

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
    public void findAverageBloodSugarDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 210);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 220);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 230);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 240);
        //when
        double averageBloodSugar = dietRepository.findAverageBloodSugarOfDiet(me.getId()).orElseThrow(() -> new NullPointerException("아예 혈당이 없다."));

        //then
        logger.info(String.valueOf(averageBloodSugar));
        assertThat(averageBloodSugar).isCloseTo(220.0, Offset.offset(0.005)); //부동 소수점은 isCloseTo()를 활용하여 판단해야 합니다.
    }

    @Transactional
    @Test
    public void findFoodHigherThanAverageBloodSugarOfDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 210);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 220);//<-평균 혈당
        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 230);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 240);

        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "chicken");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "cola");//<-
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet4.getDietId()), "ham");//<-
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet5.getDietId()), "apple");//<-

        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "orange");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "sausage");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "egg");//<-
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet4.getDietId()), "water");//<-
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet5.getDietId()), "juice");//<-

        //when
        List<String> foodNames = foodRepository.findFoodHigherThanAverageBloodSugarOfDiet(me.getId());

        //then
        logger.info(foodNames.toString());
        assertThat(foodNames.contains("cola")).isTrue();
        assertThat(foodNames.contains("ham")).isTrue();
        assertThat(foodNames.contains("apple")).isTrue();
        assertThat(foodNames.contains("egg")).isTrue();
        assertThat(foodNames.contains("water")).isTrue();
        assertThat(foodNames.contains("juice")).isTrue();
    }

}
