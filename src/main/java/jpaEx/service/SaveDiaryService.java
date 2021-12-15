package jpaEx.service;

import jpaEx.domain.diary.EntityId;
import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
import jpaEx.domain.diary.diet.Diet;
import jpaEx.domain.diary.diet.DietRepository;
import jpaEx.domain.diary.diet.EatTime;
import jpaEx.domain.diary.food.Food;
import jpaEx.domain.diary.food.FoodRepository;
import jpaEx.domain.diary.writer.Role;
import jpaEx.domain.diary.writer.Writer;
import jpaEx.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SaveDiaryService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final WriterRepository writerRepository;
    private final DiaryRepository diaryRepository;
    private final DietRepository dietRepository;
    private final FoodRepository foodRepository;

    public SaveDiaryService(WriterRepository writerRepository, DiaryRepository diaryRepository, DietRepository dietRepository, FoodRepository foodRepository) {
        this.writerRepository = writerRepository;
        this.diaryRepository = diaryRepository;
        this.dietRepository = dietRepository;
        this.foodRepository = foodRepository;
    }

    //작성자 id 생성 메서드
    public EntityId<Writer> getNextIdOfWriter() {
        Long count = writerRepository.findCountOfId();
        Long writerId;
        if (count == 0) {
            writerId = 0L;
        } else {
            writerId = writerRepository.findMaxOfId();
        }
        return new EntityId<>(writerId + 1);
    }

    //일지 id 생성 메서드 (트랜잭션 필수)
    public EntityId<DiabetesDiary> getNextIdOfDiary() {
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
    public EntityId<Diet> getNextIdOfDiet() {
        Long count = dietRepository.findCountOfId();
        Long dietId;
        if (count == 0) {
            dietId = 0L;
        } else {
            dietId = dietRepository.findMaxOfId();
        }
        return new EntityId<>(dietId + 1);
    }

    //음식 id 생성 메서드 (트랜잭션 필수)
    public EntityId<Food> getNextIdOfFood() {
        Long count = foodRepository.findCountOfId();
        Long foodId;
        if (count == 0) {
            foodId = 0L;
        } else {
            foodId = foodRepository.findMaxOfId();
        }
        return new EntityId<>(foodId + 1);
    }

    // getIdOfXXX()의 경우 트랜잭션 처리 안하면 다른 스레드가 껴들어 올 경우 id 값이 중복될 수 있어 기본키 조건을 위배할 수도 있다. 레이스 컨디션 반드시 예방해야 함.
    @Transactional
    public Writer saveWriter(String name, String email, Role role) {
        logger.info("saveWriter");
        Writer writer = new Writer(getNextIdOfWriter(), name, email, role);
        writerRepository.save(writer);
        return writer;
    }

    @Transactional
    public DiabetesDiary saveDiary(Writer writer, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        logger.info("saveDiary");
        DiabetesDiary diary = new DiabetesDiary(getNextIdOfDiary(), writer, fastingPlasmaGlucose, remark, writtenTime);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diary;
    }

    @Transactional
    public Diet saveDiet(Writer writer, DiabetesDiary diary, EatTime eatTime, int bloodSugar) {
        logger.info("saveDiet");
        Diet diet = new Diet(getNextIdOfDiet(), diary, eatTime, bloodSugar);
        diary.addDiet(diet);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diet;
    }

    @Transactional
    public Food saveFood(Writer writer, Diet diet, String foodName) {
        logger.info("saveFood");
        Food food = new Food(getNextIdOfFood(), diet, foodName);
        diet.addFood(food);
        writerRepository.save(writer);
        return food;
    }

}
