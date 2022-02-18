/*
 * @(#)ReadDiaryTest.java        1.0.9 2022/2/19
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.dasd412.remake.api.domain.diary.food.AmountUnit;
import com.dasd412.remake.api.controller.security.domain_view.FoodPageVO;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import org.assertj.core.data.Percentage;
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
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.persistence.NoResultException;

import static com.dasd412.remake.api.domain.diary.PredicateMaker.*;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security 적용 없이 리포지토리를 접근하여 조회 테스트.
 *
 * @author 양영준
 * @version 1.0.9 2022년 2월 19일
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@TestPropertySource(locations = "classpath:application-test.properties")
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

    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    /**
     * 이 테스트에서 공통적으로 쓰이는 데이터들
     */
    Writer me;

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
        logger.info("end\n");
        List<Long> writerIds = writerRepository.findAll().stream().map(Writer::getId).collect(Collectors.toList());
        writerIds.forEach(id -> writerRepository.bulkDeleteWriter(id));
    }

    @Transactional
    @Test
    public void findByIdOfWriter() {

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
        //when
        Writer found = writerRepository.findById(me.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 존재하지 않습니다."));
        Writer foundInDiary = diaryRepository.findWriterOfDiary(diary1.getId()).orElseThrow(() -> new NoResultException("해당 작성자가 존재하지 않습니다."));

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(foundInDiary).isEqualTo(found);
        assertThat(foundInDiary.getName()).isEqualTo(found.getName());
        assertThat(foundInDiary.getEmail()).isEqualTo(found.getEmail());
        assertThat(foundInDiary.getRole()).isEqualTo(found.getRole());
        logger.info(foundInDiary.toString());
    }

    @Transactional
    @Test
    public void findDiariesOfWriter() {
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

        assertThat(diaries.get(2)).isEqualTo(diary3);
        assertThat(diaries.get(2).getWriter()).isEqualTo(me);
        assertThat(diaries.get(2).getFastingPlasmaGlucose()).isEqualTo(diary3.getFastingPlasmaGlucose());
        assertThat(diaries.get(2).getRemark()).isEqualTo(diary3.getRemark());
    }

    @Transactional
    @Test
    public void findDiaryOfWriterByBothId() {
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
        //when
        List<DiabetesDiary> diaries = diaryRepository.findFpgHigherOrEqual(me.getId(), 120);

        //then
        logger.info(diaries.toString());
        assertThat(diaries.size()).isEqualTo(2);
    }

    @Transactional
    @Test
    public void findFpgLowerOrEqual() {
        //when
        List<DiabetesDiary> diaries = diaryRepository.findFpgLowerOrEqual(me.getId(), 110);

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
        //when
        List<Diet> dietList = dietRepository.findDietsInDiary(me.getId(), diary1.getId());

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
        //when
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet3.getDietId())
                .orElseThrow(() -> new NoResultException("해당 식단이 존재하지 않습니다."));

        //then
        logger.info(diet.toString());
        assertThat(diet.getEatTime()).isEqualTo(diet3.getEatTime());
        assertThat(diet.getBloodSugar()).isEqualTo(diet3.getBloodSugar());
    }

    @Transactional
    @Test
    public void findHigherThanBloodSugarBetweenTime() {
        //given
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySign(InequalitySign.GREAT_OR_EQUAL, 150));
        predicates.add(decideBetweenInDiet(LocalDateTime.of(2021, 12, 5, 0, 0, 0), LocalDateTime.of(2021, 12, 27, 0, 0, 0)));
        //when
        List<Diet> dietList = dietRepository.findDietsWithWhereClause(me.getId(), predicates);

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
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySign(InequalitySign.LESSER_OR_EQUAL, 150));
        predicates.add(decideBetweenInDiet(LocalDateTime.of(2021, 12, 5, 0, 0, 0), LocalDateTime.of(2021, 12, 27, 0, 0, 0)));
        //when
        List<Diet> dietList = dietRepository.findDietsWithWhereClause(me.getId(), predicates);

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
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySign(InequalitySign.GREAT_OR_EQUAL, 120));
        predicates.add(decideEatTime(EatTime.Lunch));

        //when
        List<Diet> dietList = dietRepository.findDietsWithWhereClause(me.getId(), predicates);

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
        //when
        List<Food> foodList = foodRepository.findFoodsInDiet(me.getId(), diet1.getDietId());

        //then
        logger.info(foodList.toString());
        assertThat(foodList.size()).isEqualTo(1);
        assertThat(foodList.contains(food1)).isTrue();
    }

    @Transactional
    @Test
    public void findOneFoodByIdInDiet() {
        //when
        Food foundFood = foodRepository.findOneFoodByIdInDiet(me.getId(), diet3.getDietId(), food3.getId())
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
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideEqualitySign(InequalitySign.GREAT_OR_EQUAL, 150));

        //when
        List<String> foodNames = foodRepository.findFoodNamesInDiet(me.getId(), predicates);

        //then
        logger.info(foodNames.toString());
        assertThat(foodNames.size()).isEqualTo(4);
        assertThat(foodNames.contains("apple")).isTrue();
        assertThat(foodNames.contains("cheese")).isTrue();
        assertThat(foodNames.contains("cola")).isTrue();
        assertThat(foodNames.contains("orange")).isTrue();
    }

    @Transactional
    @Test
    public void findAverageBloodSugarDiet() {
        //when
        double averageBloodSugar = dietRepository.findAverageBloodSugarOfDiet(me.getId()).orElseThrow(() -> new NullPointerException("아예 혈당이 없다."));

        //then
        logger.info(String.valueOf(averageBloodSugar));
        assertThat(averageBloodSugar).isCloseTo(142.0, Offset.offset(0.005)); //부동 소수점은 isCloseTo()를 활용하여 판단해야 합니다.
    }

    @Transactional
    @Test
    public void findFoodHigherThanAverageBloodSugarOfDiet() {
        //given
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideAverageOfDiet(InequalitySign.GREAT_OR_EQUAL));

        //when
        List<String> foodNames = foodRepository.findFoodNamesInDiet(me.getId(), predicates);

        //then
        logger.info(foodNames.toString());
        assertThat(foodNames.contains("apple")).isTrue();
        assertThat(foodNames.contains("cheese")).isTrue();
        assertThat(foodNames.contains("cola")).isTrue();
        assertThat(foodNames.contains("orange")).isTrue();
    }

    @Transactional
    @Test
    public void InvalidSignAverageBloodSugarOfDiet() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("평균을 구할 땐 '=='과 'none' 은 사용할 수 없다.");
        //given
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(decideAverageOfDiet(InequalitySign.EQUAL));

        //when
        foodRepository.findFoodNamesInDiet(me.getId(), predicates);
    }

    @Transactional
    @Test
    public void findDiabetesDiariesOfWriterWithRelation() {
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
        int fpg2 = 120;
        int fpg3 = 140;

        //when
        Double averageFpg = diaryRepository.findAverageFpg(me.getId()).orElseThrow(NoResultException::new);

        //then
        logger.info(averageFpg.toString());
        assertThat(averageFpg).isCloseTo((fpg1 + fpg2 + fpg3) / 3.0, Percentage.withPercentage(0.5));
    }

    @Transactional
    @Test
    public void findAverageBloodSugarGroupByEatTime() {
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
                    assertThat(average).isCloseTo((130 + 120 + 150) / 3.0, Percentage.withPercentage(0.5));
                    break;
                case Lunch:
                    assertThat(average).isCloseTo((100 + 200 + 120) / 3.0, Percentage.withPercentage(0.5));
                    break;
                case Dinner:
                    assertThat(average).isCloseTo((150 + 170 + 140) / 3.0, Percentage.withPercentage(0.5));
                    break;
            }
        }

    }

    @Transactional
    @Test
    public void findAverageFpgBetweenTime() {
        //given
        LocalDateTime startDate = LocalDateTime.of(2021, 12, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 11, 0, 0);

        //when
        Double averageFpgBetween = diaryRepository.findAverageFpgBetweenTime(me.getId(), startDate, endDate).orElseThrow(NoResultException::new);

        ///then
        assertThat(averageFpgBetween).isCloseTo(110.0, Percentage.withPercentage(0.5));

    }

    @Transactional
    @Test
    public void findAverageBloodSugarOfDietBetweenTime() {
        //given
        LocalDateTime startDate = LocalDateTime.of(2021, 12, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 27, 0, 0);

        //when
        Double averageBloodSugar = dietRepository.findAverageBloodSugarOfDietBetweenTime(me.getId(), startDate, endDate).orElseThrow(NoResultException::new);

        //then
        assertThat(averageBloodSugar).isCloseTo(150.0, Percentage.withPercentage(0.5));
    }


    @Transactional
    @Test
    public void findAverageBloodSugarGroupByEatTimeBetweenTime() {
        //given
        LocalDateTime startDate = LocalDateTime.of(2021, 12, 9, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 27, 0, 0);

        //when
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

    @Transactional
    @Test
    public void findFoodsWithPaginationBetweenTimeWithPredicate() {
        //given
        Writer other = saveDiaryService.saveWriter("other", "other@NAVER.COM", Role.User);
        DiabetesDiary diaryOther1 = saveDiaryService.saveDiaryOfWriterById(EntityId.of(Writer.class, other.getId()), 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet dietOther1 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EatTime.BreakFast, 100);
        Diet dietOther2 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EatTime.Lunch, 120);
        Diet dietOther3 = saveDiaryService.saveDietOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EatTime.Dinner, 140);

        IntStream.range(0, 30).forEach(i -> {
            switch (i % 3) {
                case 0:
                    saveDiaryService.saveFoodAndAmountOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EntityId.of(Diet.class, dietOther1.getDietId()), "diet1 food" + i, 100 + i, AmountUnit.g);
                    break;
                case 1:
                    saveDiaryService.saveFoodAndAmountOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EntityId.of(Diet.class, dietOther2.getDietId()), "diet2 food" + i, 100 + i, AmountUnit.count);
                    break;

                case 2:
                    saveDiaryService.saveFoodAndAmountOfWriterById(EntityId.of(Writer.class, other.getId()), EntityId.of(DiabetesDiary.class, diaryOther1.getId()), EntityId.of(Diet.class, dietOther3.getDietId()), "diet3 food" + i, 100 + i, AmountUnit.mL);
                    break;
            }

        });

        LocalDateTime startDate = LocalDateTime.of(2021, 11, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2021, 12, 11, 0, 0);

        FoodPageVO foodPageVO = new FoodPageVO();
        Pageable pageable = foodPageVO.makePageable();

        List<Predicate> betweenAndSugar = new ArrayList<>();
        betweenAndSugar.add(decideEqualitySign(InequalitySign.GREAT_OR_EQUAL, 120));
        betweenAndSugar.add(decideBetween(startDate, endDate));

        //when
        logger.info("select\n");
        Page<FoodBoardDTO> result1 = foodRepository.findFoodsWithPagination(other.getId(), betweenAndSugar, pageable);

        //then
        logger.info(result1.getContent().toString());
        assertThat(result1.getContent().size()).isEqualTo(10);
    }

}
