package refactoringAPI.service;

import refactoringAPI.domain.diary.EntityId;
import refactoringAPI.domain.diary.diabetesDiary.DiabetesDiary;
import refactoringAPI.domain.diary.diabetesDiary.DiaryRepository;
import refactoringAPI.domain.diary.diet.Diet;
import refactoringAPI.domain.diary.diet.DietRepository;
import refactoringAPI.domain.diary.diet.EatTime;
import refactoringAPI.domain.diary.food.Food;
import refactoringAPI.domain.diary.food.FoodRepository;
import refactoringAPI.domain.diary.writer.Writer;
import refactoringAPI.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;

@Service
public class UpdateDeleteDiaryService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final FoodRepository foodRepository;
    private final DietRepository dietRepository;
    private final DiaryRepository diaryRepository;
    private final WriterRepository writerRepository;

    public UpdateDeleteDiaryService(FoodRepository foodRepository, DietRepository dietRepository, DiaryRepository diaryRepository, WriterRepository writerRepository) {
        this.foodRepository = foodRepository;
        this.dietRepository = dietRepository;
        this.diaryRepository = diaryRepository;
        this.writerRepository = writerRepository;
    }

    @Transactional
    public DiabetesDiary updateDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, int fastingPlasmaGlucose, String remark) {
        logger.info("update diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");

        DiabetesDiary targetDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));

        targetDiary.update(fastingPlasmaGlucose, remark);

        return targetDiary;
    }

    @Transactional
    public void deleteDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("delete diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");

        Writer writer = writerRepository.findById(writerEntityId.getId()).orElseThrow(() -> new NoSuchElementException("해당 작성자가 없습니다."));

        DiabetesDiary targetDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));

        //orphanRemoval = true 로 해놓았기 때문에 부모의 컬렉션에서 자식이 null 되면 알아서 delete 쿼리가 나간다.
        targetDiary.getDietList().clear();
        writer.removeDiary(targetDiary);

    }

    @Transactional
    public Diet updateDiet(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, EatTime eatTime, int bloodSugar) {
        logger.info("update diet");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");

        Diet targetDiet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 식단이 존재하지 않습니다."));

        targetDiet.update(eatTime, bloodSugar);

        return targetDiet;
    }

    @Transactional
    public void deleteDiet(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId) {
        logger.info("delete diet");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");

        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));

        Diet targetDiet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 식단이 존재하지 않습니다."));

        //orphanRemoval = true 로 해놓았기 때문에 부모의 컬렉션에서 자식이 null 되면 알아서 delete 쿼리가 나간다.
        targetDiet.getFoodList().clear();
        diary.removeDiet(targetDiet);
    }

    @Transactional
    public Food updateFood(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, EntityId<Food, Long> foodEntityId, String foodName) {
        logger.info("update food");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        checkNotNull(foodEntityId, "foodId must be provided");

        Food targetFood = foodRepository.findOneFoodByIdInDiet(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId(), foodEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 음식이 존재하지 않습니다."));

        targetFood.update(foodName);

        return targetFood;
    }

    @Transactional
    public void deleteFood(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, EntityId<Diet, Long> dietEntityId, EntityId<Food, Long> foodEntityId) {
        logger.info("delete food");
        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkNotNull(dietEntityId, "dietId must be provided");
        checkNotNull(foodEntityId, "foodId must be provided");

        Diet diet = dietRepository.findOneDietByIdInDiary(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 식단이 존재하지 않습니다."));

        Food targetFood = foodRepository.findOneFoodByIdInDiet(writerEntityId.getId(), diaryEntityId.getId(), dietEntityId.getId(), foodEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 음식이 존재하지 않습니다."));

        //orphanRemoval = true 로 해놓았기 때문에 부모의 컬렉션에서 자식이 null 되면 알아서 delete 쿼리가 나간다.
        diet.removeFood(targetFood);
    }
}
