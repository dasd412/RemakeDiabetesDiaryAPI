/*
 * @(#)ReadDiaryTest.java        1.0.4 2022/2/4
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.querydsl.core.Tuple;
import org.assertj.core.data.Percentage;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security 적용 없이 리포지토리를 접근하여 조회 테스트.
 *
 * @author 양영준
 * @version 1.0.4 2022년 2월 4일
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
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
        logger.info("end\n");
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
    }

    @Transactional
    @Test
    public void maxOfIdWhenEmpty() {
        //given
        Long maxId = writerRepository.findMaxOfId();
        logger.info("maxId : " + maxId);
        assertThat(maxId).isNull();
    }

    //n+1문제 발생. n+1 문제를 테스트하려면 @Transactional 을 제거해야 한다.
    @Test
    public void findDiaryOfWriterIsFetchJoin() {
        //given
        Writer me = saveDiaryService.saveWriter("ME", "TEST@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test", LocalDateTime.now());
        Diet diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 250);
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "chicken");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "cola");

        //when
        DiabetesDiary foundDiary = diaryRepository.findDiabetesDiaryOfWriterWithRelation(me.getId(), diary.getId()).orElseThrow(() -> new NoResultException("존재하지 않는 일지입니다."));

        //then
        assertThat(Hibernate.isInitialized(foundDiary.getDietList())).isTrue();
        assertThat(Hibernate.isInitialized(foundDiary.getDietList().get(0).getFoodList())).isTrue();
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
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 존재하지 않습니다."));

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
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 존재하지 않습니다."));
        Writer foundOfDiary = diaryRepository.findWriterOfDiary(diary.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 존재하지 않습니다."));
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
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 10, "test2", LocalDateTime.now());
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 40, "test3", LocalDateTime.now());

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary2.getId()).orElseThrow(() -> new NoResultException("존재하지 않는 일지입니다."));

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
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
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
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Dinner, 150);

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary.getId(), diet3.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));

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

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        Diet diet7 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

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

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        Diet diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

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

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        Diet diet8 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

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
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "pizza");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "chicken");
        Food food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "cola");

        //when
        Food foundFood = foodRepository.findOneFoodByIdInDiet(me.getId(), diet.getDietId(), food3.getId())
                .orElseThrow(() -> new NoResultException("음식 없음."));

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
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "chicken");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet1.getDietId()), "cola");

        Diet diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "ham");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "chicken");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet2.getDietId()), "cola");

        Diet diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 150);
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "ham");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "egg");
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet3.getDietId()), "water");

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

    @Transactional
    @Test
    public void findDiabetesDiariesOfWriterWithRelation() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

        //when
        List<DiabetesDiary> diariesWithRelation = diaryRepository.findDiabetesDiariesOfWriterWithRelation(me.getId());
        //then
        logger.info(diariesWithRelation.toString());
        for (DiabetesDiary diary : diariesWithRelation) {
            logger.info(diary.getDietList().toString());
        }
        assertThat(diariesWithRelation.size()).isEqualTo(3);
        for (DiabetesDiary diary : diariesWithRelation) {
            assertThat(diary.getDietList().size()).isEqualTo(3);
        }

    }

    @Transactional
    @Test
    public void findDiariesWithRelationBetweenTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

        LocalDateTime startDate = LocalDateTime.of(2021, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 11, 0, 0);

        //when
        List<DiabetesDiary> diaries = diaryRepository.findDiariesWithRelationBetweenTime(me.getId(), startDate, endDate);

        //then
        logger.info(diaries.toString());
        for (DiabetesDiary diary : diaries) {
            logger.info(diary.getDietList().toString());
        }
        assertThat(diaries.size()).isEqualTo(2);
        for (DiabetesDiary diary : diaries) {
            assertThat(diary.getDietList().size()).isEqualTo(3);
        }
    }

    @Transactional
    @Test
    public void findAverageFpg() {
        //given
        int fpg1 = 100;
        int fpg2 = 200;
        int fpg3 = 100;
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg1, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg2, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg3, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        //when
        Double averageFpg = diaryRepository.findAverageFpg(me.getId()).orElseThrow(NoResultException::new);

        //then
        logger.info(averageFpg.toString());
        assertThat(averageFpg).isCloseTo((fpg1 + fpg2 + fpg3) / 3.0, Percentage.withPercentage(0.5));
    }

    @Transactional
    @Test
    public void findAverageBloodSugarGroupByEatTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        int br1 = 100;
        int lu1 = 100;
        int di1 = 100;
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, br1);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, lu1);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, di1);

        int br2 = 120;
        int lu2 = 200;
        int di2 = 170;
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, br2);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, lu2);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, di2);

        int br3 = 150;
        int lu3 = 120;
        int di3 = 140;
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, br3);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, lu3);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, di3);

        //when
        List<Tuple> tupleList = dietRepository.findAverageBloodSugarGroupByEatTime(me.getId());

        //then
        logger.info(tupleList.toString());
        for (Tuple tuple : tupleList) {
            EatTime eatTime = tuple.get(QDiet.diet.eatTime);
            Double average = tuple.get(QDiet.diet.bloodSugar.avg());
            logger.info(eatTime + " " + average);
            switch (Objects.requireNonNull(eatTime)) {
                case BreakFast:
                    assertThat(average).isCloseTo((br1 + br2 + br3) / 3.0, Percentage.withPercentage(0.5));
                    break;
                case Lunch:
                    assertThat(average).isCloseTo((lu1 + lu2 + lu3) / 3.0, Percentage.withPercentage(0.5));
                    break;
                case Dinner:
                    assertThat(average).isCloseTo((di1 + di2 + di3) / 3.0, Percentage.withPercentage(0.5));
                    break;
            }
        }

    }

    @Transactional
    @Test
    public void findAverageFpgBetweenTime() {
        //given
        int fpg1 = 100;
        int fpg2 = 200;
        int fpg3 = 100;
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg1, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg2, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), fpg3, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        LocalDateTime startDate = LocalDateTime.of(2021, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 11, 0, 0);

        //when
        logger.info("select \n");
        Double averageFpgBetween = diaryRepository.findAverageFpgBetweenTime(me.getId(), startDate, endDate).orElseThrow(NoResultException::new);

        ///then
        assertThat(averageFpgBetween).isCloseTo(150.0, Percentage.withPercentage(0.5));

    }

    @Transactional
    @Test
    public void findAverageBloodSugarOfDietBetweenTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

        LocalDateTime startDate = LocalDateTime.of(2021, 12, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 27, 0, 0);

        //when
        logger.info("select \n");
        Double averageBloodSugar = dietRepository.findAverageBloodSugarOfDietBetweenTime(me.getId(), startDate, endDate).orElseThrow(NoResultException::new);

        //then
        assertThat(averageBloodSugar).isCloseTo(150.0, Percentage.withPercentage(0.5));
    }


    @Transactional
    @Test
    public void findAverageBloodSugarGroupByEatTimeBetweenTime() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        DiabetesDiary diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        DiabetesDiary diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 20, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 100);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

        LocalDateTime startDate = LocalDateTime.of(2021, 12, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 27, 0, 0);

        //when
        logger.info("select \n");
        List<Tuple> tupleList = dietRepository.findAverageBloodSugarGroupByEatTimeBetweenTime(me.getId(), startDate, endDate);

        //then
        assertThat(tupleList.size()).isEqualTo(3);
        for (Tuple tuple : tupleList) {
            EatTime eatTime = tuple.get(QDiet.diet.eatTime);
            Double average = tuple.get(QDiet.diet.bloodSugar.avg());
            logger.info(eatTime + " " + average);
            switch (Objects.requireNonNull(eatTime)) {
                case BreakFast:
                    assertThat(average).isCloseTo(135.0, Percentage.withPercentage(0.5));
                    break;
                case Lunch:
                    assertThat(average).isCloseTo(160.0, Percentage.withPercentage(0.5));
                    break;
                case Dinner:
                    assertThat(average).isCloseTo(155.0, Percentage.withPercentage(0.5));
                    break;
            }
        }
    }

}
