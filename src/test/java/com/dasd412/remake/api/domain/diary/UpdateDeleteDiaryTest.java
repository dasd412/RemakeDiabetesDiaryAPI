/*
 * @(#)UpdateDeleteDiaryTest.java        1.1.1 2022/2/28
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.NoResultException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security 적용 없이 리포지토리를 접근하여 수정 및 삭제 테스트.
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 28일
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
public class UpdateDeleteDiaryTest {

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

    @Autowired
    ProfileRepository profileRepository;

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 이 테스트에서 공통적으로 쓰이는 데이터들
     */
    Writer me;
    Profile profile;

    DiabetesDiary diary1;
    DiabetesDiary diary2;
    DiabetesDiary diary3;

    Diet diet1;
    Diet diet2;
    Diet diet3;
    Diet diet4;
    Diet diet5;
    Diet diet6;
    Diet diet7;
    Diet diet8;
    Diet diet9;

    Food food1;
    Food food2;
    Food food3;
    Food food4;
    Food food5;
    Food food6;
    Food food7;
    Food food8;
    Food food9;

    @Before
    public void setUp() {
        //given
        me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        profile = new Profile(DiabetesPhase.NORMAL);
        profileRepository.save(profile);
        me.setProfile(profile);
        writerRepository.save(me);

        diary1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 100, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        diary2 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 120, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        diary3 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, me.getId()), 140, "test1", LocalDateTime.of(2021, 12, 25, 0, 0, 0));

        diet1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.BreakFast, 130);
        diet2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Lunch, 100);
        diet3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EatTime.Dinner, 150);

        diet4 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.BreakFast, 120);
        diet5 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Lunch, 200);
        diet6 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EatTime.Dinner, 170);

        diet7 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.BreakFast, 150);
        diet8 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Lunch, 120);
        diet9 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EatTime.Dinner, 140);

        food1 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()), "pizza");
        food2 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet2.getDietId()), "chicken");
        food3 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet3.getDietId()), "cola");

        food4 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet4.getDietId()), "ham");
        food5 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet5.getDietId()), "apple");
        food6 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet6.getDietId()), "cheese");

        food7 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EntityId.of(Diet.class, diet7.getDietId()), "orange");
        food8 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EntityId.of(Diet.class, diet8.getDietId()), "sausage");
        food9 = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()), EntityId.of(Diet.class, diet9.getDietId()), "egg");
        logger.info("set up end\n");
    }


    @After
    public void clean() {
        List<Long> writerIds = writerRepository.findAll().stream().map(Writer::getId).collect(Collectors.toList());
        writerIds.forEach(id -> writerRepository.bulkDeleteWriter(id));
    }

    @Transactional
    @Test
    public void updateDiary() {
        //given
        updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), 100, "modifyTest");

        //when
        Writer found = writerRepository.findAll().get(0);
        DiabetesDiary foundDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary2.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(1).getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(found.getDiaries().get(1).getRemark()).isEqualTo("modifyTest");
        logger.info(found.getDiaries().get(1).toString());

        assertThat(foundDiary.getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(foundDiary.getRemark()).isEqualTo("modifyTest");
        logger.info(foundDiary.toString());
    }

    @Transactional
    @Test
    public void deleteDiary() {
        //given
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.getDiaries().toString());

        assertThat(found.getDiaries().size()).isEqualTo(2);
        assertThat(diaries.size()).isEqualTo(2);
    }

    @Transactional
    @Test
    public void deleteSomeDiary() {
        //given
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        assertThat(found.getDiaries().size()).isEqualTo(1);
        assertThat(diaries.size()).isEqualTo(1);

        logger.info(found.toString());
        logger.info(found.getDiaries().toString());
        logger.info(diaries.toString());
    }

    /**
     * 작성자 삭제되면 일지도 삭제되는 지 cascade 테스트
     */
    @Transactional
    @Test
    public void deleteWriterCascadeDiary() {
        //given
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
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoResultException("일지 없음"));
        List<Diet> dietList = dietRepository.findAll();

        //then
        logger.info(diary.getDietList().toString());
        assertThat(diary.getDietList().size()).isEqualTo(2);
        assertThat(dietList.size()).isEqualTo(8);
    }

    @Transactional
    @Test
    public void deleteSomeDiet() {
        //given
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
        assertThat(dietList.size()).isEqualTo(6);
    }

    @Transactional
    @Test
    public void deleteWriterCascadeDiet() {
        //given
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
    public void deleteWriterCascadeProfile() {
        //given
        writerRepository.bulkDeleteWriter(me.getId());

        //when
        List<Writer> writers = writerRepository.findAll();
        List<Profile> profiles = profileRepository.findAll();

        //then
        assertThat(writers.size()).isEqualTo(0);
        assertThat(profiles.size()).isEqualTo(0);

    }

    @Transactional
    @Test
    public void deleteDiaryCascadeDiet() {
        //given
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()));

        //when
        List<Writer> found = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();

        //then
        assertThat(found.get(0).getDiaries().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void updateFood() {
        //given
        updateDeleteDiaryService.updateFood(EntityId.of(Writer.class, me.getId()), EntityId.of(Diet.class, diet1.getDietId()), EntityId.of(Food.class, food1.getId()), "chicken");

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet1.getDietId())
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
    public void updateProfile() {
        //given
        updateDeleteDiaryService.updateProfile(EntityId.of(Writer.class, me.getId()), DiabetesPhase.PRE_DIABETES);

        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(NoResultException::new);
        Profile targetProfile = writerRepository.findProfile(me.getId()).orElseThrow(NoResultException::new);

        //then
        logger.info(found.toString());
        assertThat(found.getProfile().getDiabetesPhase()).isEqualTo(DiabetesPhase.PRE_DIABETES);
        logger.info(targetProfile.toString());
        assertThat(targetProfile.getDiabetesPhase()).isEqualTo(DiabetesPhase.PRE_DIABETES);
    }

    @Transactional
    @Test
    public void deleteFood() {
        //given
        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()), EntityId.of(Food.class, food1.getId()));

        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet1.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));
        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(diet.getFoodList().toString());
        assertThat(diet.getFoodList().size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(8);
    }

    @Transactional
    @Test
    public void deleteSomeFood() {
        //given
        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet2.getDietId()), EntityId.of(Food.class, food2.getId()));
        updateDeleteDiaryService.deleteFood(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet4.getDietId()), EntityId.of(Food.class, food4.getId()));

        //when
        Diet foundDiet1 = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet2.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));

        Diet foundDiet2 = dietRepository.findOneDietByIdInDiary(me.getId(), diary2.getId(), diet4.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));

        List<Food> foodList = foodRepository.findAll();

        //then
        logger.info(foundDiet1.getFoodList().toString());
        assertThat(foundDiet1.getFoodList().size()).isEqualTo(0);

        logger.info(foundDiet2.getFoodList().toString());
        assertThat(foundDiet2.getFoodList().size()).isEqualTo(0);

        logger.info(foodList.toString());
        assertThat(foodList.size()).isEqualTo(7);
        assertThat(foodList.contains(food2)).isFalse();
        assertThat(foodList.contains(food4)).isFalse();
    }

    @Transactional
    @Test
    public void deleteWriterCascadeFood() {
        //given
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
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()));

        //when
        List<Writer> found = writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        //then
        assertThat(found.get(0).getDiaries().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
        assertThat(foodList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteDietCascadeFood() {
        //given
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));

        //when
        List<Diet> dietList = dietRepository.findAll();
        List<Food> foodList = foodRepository.findAll();

        //then
        assertThat(dietList.size()).isEqualTo(8);
        assertThat(foodList.size()).isEqualTo(8);
    }

    /*
    예외 캐치 테스트
     */
    @Transactional
    @Test
    public void updateDiaryBloodSugarInappropriate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 0 and 1000");

        Writer other = saveDiaryService.saveWriter("other", "other@NAVER.COM", Role.User);
        DiabetesDiary diaryOther = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 200, "test", LocalDateTime.now());
        updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther.getId()), -190, "modifyTest");
    }

    @Transactional
    @Test
    public void updateDietBloodSugarInappropriate() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 0 and 1000");

        Writer other = saveDiaryService.saveWriter("other", "other@NAVER.COM", Role.User);
        DiabetesDiary diaryOther = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet dietOther = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther.getId()), EatTime.BreakFast, 100);
        updateDeleteDiaryService.updateDiet(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther.getId()), EntityId.of(Diet.class, dietOther.getDietId()), EatTime.Lunch, -1900);
    }

    @Transactional
    @Test
    public void updateFoodNoName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");

        Writer other = saveDiaryService.saveWriter("other", "other@NAVER.COM", Role.User);
        DiabetesDiary diaryOther = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 20, "test", LocalDateTime.now());
        Diet dietOther = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther.getId()), EatTime.Lunch, 250);
        Food foodOther = saveDiaryService.saveFoodOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther.getId()), EntityId.of(Diet.class, dietOther.getDietId()), "pizza");
        updateDeleteDiaryService.updateFood(EntityId.of(Writer.class, other.getId()), EntityId.of(Diet.class, dietOther.getDietId()), EntityId.of(Food.class, foodOther.getId()), "");
    }

}
