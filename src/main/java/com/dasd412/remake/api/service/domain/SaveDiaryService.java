/*
 * @(#)SaveDiaryService.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.domain;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.controller.security.domain_rest.dto.diary.SecurityDiaryPostRequestDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiaryRepository;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.DietRepository;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.FoodRepository;
import com.dasd412.remake.api.domain.diary.profile.DiabetesPhase;
import com.dasd412.remake.api.domain.diary.profile.Profile;
import com.dasd412.remake.api.domain.diary.profile.ProfileRepository;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.util.DateStringJoiner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;

import static com.google.common.base.Preconditions.checkNotNull;

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

    /**
     * JSON 직렬화가 LocalDateTime 에는 적용이 안되서 작성한 헬프 메서드.
     *
     * @param dto 작성된 일지 정보 (일지, 식단, 음식 모두 포함)
     * @return String => LocalDateTime
     */
    private LocalDateTime convertStringToLocalDateTime(SecurityDiaryPostRequestDTO dto) {
        DateStringJoiner dateStringJoiner = DateStringJoiner.builder()
                .year(dto.getYear()).month(dto.getMonth()).day(dto.getDay())
                .hour(dto.getHour()).minute(dto.getMinute()).second(dto.getSecond())
                .build();

        return dateStringJoiner.convertLocalDateTime();
    }

    /**
     * 1.17 버전부터 일지 저장에 쓰이는 서비스 로직
     * 트랜잭션 하나에 넣어서 처리 (트랜잭션 오버헤드 줄이기 위함)
     *
     * @param principalDetails 로그인된 유저의 세션 정보
     * @param dto              컨트롤러에서 받아온 일지 저장용 dto
     * @return 작성된 일지의 id
     */
    @Transactional
    public Long postDiaryWithEntities(PrincipalDetails principalDetails, SecurityDiaryPostRequestDTO dto) {
        logger.info("post diary in service logic");
        checkNotNull(principalDetails, "principalDetails must be provided");

        /* 0. 현재 세션에 담긴 사용자 정보 판별 */
        Writer writer = writerRepository.findById(principalDetails.getWriter().getId()).orElseThrow(() -> new NoResultException("작성자가 없습니다."));

        /* 2-1. LocalDateTime JSON 직렬화 */
        LocalDateTime writtenTime = convertStringToLocalDateTime(dto);

        /* 2-2. 혈당 일지 저장 */
        DiabetesDiary diary = new DiabetesDiary(getNextIdOfDiary(), writer, dto.getFastingPlasmaGlucose(), dto.getRemark(), writtenTime);
        writer.addDiary(diary);

        /* 3. 아침 점심 저녁 식사 저장 */

        Diet breakFast = new Diet(getNextIdOfDiet(), diary, EatTime.BreakFast, dto.getBreakFastSugar());
        diary.addDiet(breakFast);

        Diet lunch = new Diet(getNextIdOfDiet(), diary, EatTime.Lunch, dto.getLunchSugar());
        diary.addDiet(lunch);

        Diet dinner = new Diet(getNextIdOfDiet(), diary, EatTime.Dinner, dto.getDinnerSugar());
        diary.addDiet(dinner);

        /* 4-1. 아침 음식 저장 */
        dto.getBreakFastFoods().forEach(elem -> {
            Food food = new Food(getNextIdOfFood(), breakFast, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
            breakFast.addFood(food);
        });

        /* 4-2. 점심 음식 저장 */
        dto.getLunchFoods().forEach(elem -> {
            Food food = new Food(getNextIdOfFood(), lunch, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
            lunch.addFood(food);
        });

        /* 4-3. 저녁 음식 저장 */
        dto.getDinnerFoods().forEach(elem -> {
            Food food = new Food(getNextIdOfFood(), dinner, elem.getFoodName(), elem.getAmount(), elem.getAmountUnit());
            dinner.addFood(food);
        });

        writerRepository.save(writer);

        return diary.getId();
    }

    /**
     * @param writerEntityId 작성자 id
     * @param phase          당뇨 단계
     * @return 작성자의 생성된 프로필
     */
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
