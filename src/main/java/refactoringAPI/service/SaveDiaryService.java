package refactoringAPI.service;

import refactoringAPI.domain.diary.EntityId;
import refactoringAPI.domain.diary.diabetesDiary.DiabetesDiary;
import refactoringAPI.domain.diary.diabetesDiary.DiaryRepository;
import refactoringAPI.domain.diary.diet.Diet;
import refactoringAPI.domain.diary.diet.DietRepository;
import refactoringAPI.domain.diary.diet.EatTime;
import refactoringAPI.domain.diary.food.Food;
import refactoringAPI.domain.diary.food.FoodRepository;
import refactoringAPI.domain.diary.writer.Role;
import refactoringAPI.domain.diary.writer.Writer;
import refactoringAPI.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

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

    //todo writer 는 스프링 시큐리티 적용 후 빼낼 예정
    //작성자 id 생성 메서드 (트랜잭션 필수)
    public EntityId<Writer, Long> getNextIdOfWriter() {
        Long count = writerRepository.findCountOfId();
        Long writerId;
        if (count == 0) {
            writerId = 0L;
        } else {
            writerId = writerRepository.findMaxOfId();
        }
        return EntityId.of(Writer.class, writerId + 1);
    }

    //일지 id 생성 메서드 (트랜잭션 필수)
    public EntityId<DiabetesDiary, Long> getNextIdOfDiary() {
        Long count = diaryRepository.findCountOfId();
        Long diaryId;
        if (count == 0) {
            diaryId = 0L;
        } else {
            diaryId = diaryRepository.findMaxOfId();
        }
        return EntityId.of(DiabetesDiary.class, diaryId + 1);
    }

    //식단 id 생성 메서드 (트랜잭션 필수)
    public EntityId<Diet, Long> getNextIdOfDiet() {
        Long count = dietRepository.findCountOfId();
        Long dietId;
        if (count == 0) {
            dietId = 0L;
        } else {
            dietId = dietRepository.findMaxOfId();
        }
        return EntityId.of(Diet.class, dietId + 1);
    }

    //음식 id 생성 메서드 (트랜잭션 필수)
    public EntityId<Food, Long> getNextIdOfFood() {
        Long count = foodRepository.findCountOfId();
        Long foodId;
        if (count == 0) {
            foodId = 0L;
        } else {
            foodId = foodRepository.findMaxOfId();
        }
        return EntityId.of(Food.class, foodId + 1);
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
    public DiabetesDiary saveDiaryOfWriterById(EntityId<Writer, Long> writerEntityId, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        logger.info("saveDiaryOfWriterById");
        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoSuchElementException("작성자가 없습니다."));
        DiabetesDiary diary = new DiabetesDiary(getNextIdOfDiary(), writer, fastingPlasmaGlucose, remark, writtenTime);
        writer.addDiary(diary);
        writerRepository.save(writer);
        return diary;
    }

    @Transactional
    public Diet saveDietOfWriterById(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EatTime eatTime, int bloodSugar) {
        logger.info("saveDietOfWriterById");
        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoSuchElementException("작성자가 없습니다."));
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId()).orElseThrow(() -> new NoSuchElementException("일지가 없습니다."));
        Diet diet = new Diet(getNextIdOfDiet(), diary, eatTime, bloodSugar);
        diary.addDiet(diet);
        writerRepository.save(writer);
        return diet;
    }

    @Transactional
    public Food saveFoodOfWriterById(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, String foodName) {
        logger.info("saveFoodOfWriterById");
        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoSuchElementException("작성자가 없습니다."));
        Diet diet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId()).orElseThrow(() -> new NoSuchElementException("식단이 없습니다."));
        Food food = new Food(getNextIdOfFood(), diet, foodName);
        diet.addFood(food);
        writerRepository.save(writer);
        return food;
    }
}
