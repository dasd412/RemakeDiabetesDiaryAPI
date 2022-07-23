/*
 * @(#)CreateDiaryTest.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.profile.ProfileRepository;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security 적용 없이 리포지토리를 접근하여 저장 테스트.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
public class CreateDiaryTest {

    @Autowired
    SaveDiaryService saveDiaryService;

    @Autowired
    WriterRepository writerRepository;

    @Autowired
    ProfileRepository profileRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer me;

    @Before
    public void setUp() {
        //given
        me = saveDiaryService.saveWriter("ME", "ME@NAVER.COM", Role.User);
    }

    @After
    public void clean() {
        List<Long> writerIds = writerRepository.findAll().stream().map(Writer::getId).collect(Collectors.toList());
        writerIds.forEach(id -> writerRepository.bulkDeleteWriter(id));
    }

    /*
    Save 테스트
     */
    @Transactional
    @Test
    public void saveWriterOne() {
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

        testSavingWriter("other", "OTHER@NAVER.COM", Role.User, 1);

        testSavingWriter("another", "Another@NAVER.COM", Role.User, 2);
    }

    private void testSavingWriter(String name, String email, Role role, int savedSequence) {

        //given
        Writer writer = saveDiaryService.saveWriter(name, email, role);

        //when
        Writer found = writerRepository.findAll().get(savedSequence);

        //then
        assertThat(found).isEqualTo(writer);
        assertThat(found.getName()).isEqualTo(writer.getName());
        assertThat(found.getEmail()).isEqualTo(writer.getEmail());
        assertThat(found.getRole()).isEqualTo(found.getRole());
    }


    @Transactional
    @Test
    public void saveDiaryOneOfWriterOneById() {
        //given
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
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().contains(food1)).isTrue();

        //food2
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().contains(food2)).isTrue();

        //food2
        assertThat(found.getDiaries().get(0).getDietList().get(0).getFoodList().contains(food3)).isTrue();

    }

    /*
     * 프로필 테스트
     */
    @Transactional
    @Test
    public void makeProfile() {
        //given
        Profile profile = saveDiaryService.makeProfile(EntityId.of(Writer.class, me.getId()), DiabetesPhase.PRE_DIABETES);

        //when
        List<Writer> writers = writerRepository.findAll();
        List<Profile> profiles = profileRepository.findAll();

        //then
        assertThat(writers.get(0).getName()).isEqualTo(me.getName());
        assertThat(writers.get(0).getEmail()).isEqualTo(me.getEmail());
        assertThat(writers.get(0).getProfile().getDiabetesPhase()).isEqualTo(profile.getDiabetesPhase());

        assertThat(profiles.get(0).getProfileId()).isEqualTo(profile.getProfileId());
        assertThat(profiles.get(0).getDiabetesPhase()).isEqualTo(profile.getDiabetesPhase());

    }


    /*
    예외 캐치 테스트
     */
    @Transactional
    @Test
    public void saveWriterNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        saveDiaryService.saveWriter("", "TEST@NAVER.COM", Role.User);
    }


    @Transactional
    @Test
    public void saveDiaryNegativeBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 0 and 1000");
        saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), -1, "", LocalDateTime.now());
    }


    @Transactional
    @Test
    public void saveDietNegativeBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 0 and 1000");
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "", LocalDateTime.now());
        saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, -200);
    }

    @Transactional
    @Test
    public void saveFoodNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        DiabetesDiary diary = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "", LocalDateTime.now());
        Diet diet = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EatTime.Lunch, 150);
        saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), EntityId.of(Diet.class, diet.getDietId()), "");
    }

}