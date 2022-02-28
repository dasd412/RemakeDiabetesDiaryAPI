/*
 * @(#)SaveDiaryService.java        1.1.1 2022/2/28
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.AmountUnit;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.profile.ProfileRepository;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 저장 로직을 수행하는 서비스 클래스
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 28일
 */
@Service
public class SaveDiaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;
    private final DiaryRepository diaryRepository;
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;
    private final ProfileRepository profileRepository;

    public SaveDiaryService(WriterRepository writerRepository, DiaryRepository diaryRepository, DietRepository dietRepository, FoodRepository foodRepository, ProfileRepository profileRepository) {
        this.writerRepository = writerRepository;
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
        this.profileRepository = profileRepository;
    }

    /*
    getIdOfXXX()의 경우 트랜잭션 처리 안하면 다른 스레드가 껴들어 올 경우 id 값이 중복될 수 있어 기본키 조건을 위배할 수도 있다. 레이스 컨디션 반드시 예방해야 함.
     */

    /**
     * 작성자 id 생성 메서드 (트랜잭션 필수). 시큐리티 적용 후에는 WriterService가 담당한다.
     *
     * @return 래퍼로 감싸진 작성자 id
     * @deprecated
     */
    public EntityId<Writer, Long> getNextIdOfWriter() {
        Long writerId = writerRepository.findMaxOfId();
        if (writerId == null) {
            writerId = 0L;
        }
        return EntityId.of(Writer.class, writerId + 1);
    }

    /**
     * 일지 id 생성 메서드 (트랜잭션 필수).
     *
     * @return 래퍼로 감싸진 일지 id
     */
    public EntityId<DiabetesDiary, Long> getNextIdOfDiary() {
        Long diaryId = diaryRepository.findMaxOfId();
        if (diaryId == null) {
            diaryId = 0L;
        }
        return EntityId.of(DiabetesDiary.class, diaryId + 1);
    }

    /**
     * 식단 id 생성 메서드 (트랜잭션 필수).
     *
     * @return 래퍼로 감싸진 식단 id
     */
    public EntityId<Diet, Long> getNextIdOfDiet() {
        Long dietId = dietRepository.findMaxOfId();
        if (dietId == null) {
            dietId = 0L;
        }
        return EntityId.of(Diet.class, dietId + 1);
    }

    /**
     * 음식 id 생성 메서드 (트랜잭션 필수).
     *
     * @return 래퍼로 감싸진 음식 id
     */
    public EntityId<Food, Long> getNextIdOfFood() {
        Long foodId = foodRepository.findMaxOfId();
        if (foodId == null) {
            foodId = 0L;
        }
        return EntityId.of(Food.class, foodId + 1);
    }


    @Transactional
    public Writer saveWriter(String name, String email, Role role) {
        logger.info("saveWriter");
        Writer writer = new Writer(getNextIdOfWriter(), name, email, role);
        writerRepository.save(writer);
        return writer;
    }

    @Transactional
    public DiabetesDiary saveDiaryOfWriterById(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        logger.info("saveDiaryOfWriterById");
        checkNotNull(writerEntityId, "writerId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
        DiabetesDiary diary = new DiabetesDiary(getNextIdOfDiary(), writer, fastingPlasmaGlucose, remark, writtenTime);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diary;
    }

    @Transactional
    public Diet saveDietOfWriterById(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EatTime eatTime, int bloodSugar) {
        logger.info("saveDietOfWriterById");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryEntityId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId()).orElseThrow(() -> new NoResultException("일지가 없습니다."));
        Diet diet = new Diet(getNextIdOfDiet(), diary, eatTime, bloodSugar);
        diary.addDiet(diet);
        writerRepository.save(writer);
        return diet;
    }

    @Transactional
    public Food saveFoodOfWriterById(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, String foodName) {
        logger.info("saveFoodOfWriterById");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryEntityId must be provided");
        checkNotNull(dietEntityId, "dietEntityId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
        Diet diet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId()).orElseThrow(() -> new NoResultException("식단이 없습니다."));
        Food food = new Food(getNextIdOfFood(), diet, foodName);
        diet.addFood(food);
        writerRepository.save(writer);
        return food;
    }

    @Transactional
    public void saveFoodAndAmountOfWriterById(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, String foodName, double amount, AmountUnit amountUnit) {
        logger.info("saveFoodAndAmountOfWriterById");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));
        Diet diet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId()).orElseThrow(() -> new NoResultException("식단이 없습니다."));
        Food food = new Food(getNextIdOfFood(), diet, foodName, amount, amountUnit);
        diet.addFood(food);
        writerRepository.save(writer);
    }

    @Transactional
    public Profile makeProfile(EntityId<Writer, Long> writerEntityId, DiabetesPhase phase) {
        logger.info("make profile");
        checkNotNull(writerEntityId, "writerId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));

        Profile profile = new Profile(phase);
        profileRepository.save(profile);

        writer.setProfile(profile);
        writerRepository.save(writer);

        return profile;
    }
}
