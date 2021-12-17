package jpaEx.domain.diary.writer;

import jpaEx.domain.diary.EntityId;
import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
import jpaEx.domain.diary.diet.Diet;
import jpaEx.domain.diary.diet.DietRepository;
import jpaEx.domain.diary.diet.EatTime;
import jpaEx.service.SaveDiaryService;
import jpaEx.service.UpdateDeleteDiaryService;
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean() {
        writerRepository.deleteAll();//cascade all 이므로 작성자 삭제하면 다 삭제됨.
    }

    @Transactional
    @Test
    public void updateDiary() {

        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());

        updateDeleteDiaryService.updateDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()), 100, "modifyTest");

        //when
        Writer found = writerRepository.findAll().get(0);
        DiabetesDiary foundDiary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary.getId())
                .orElseThrow(() -> new NoSuchElementException("일지 없음"));

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("modifyTest");
        logger.info(found.getDiaries().get(0).toString());

        assertThat(foundDiary.getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(foundDiary.getRemark()).isEqualTo("modifyTest");
        logger.info(foundDiary.toString());
    }

    @Transactional
    @Test
    public void deleteDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary = saveDiaryService.saveDiary(me, 20, "test", LocalDateTime.now());

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.getDiaries().toString());

        assertThat(found.getDiaries().size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteSomeDiary() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());
        DiabetesDiary diary3 = saveDiaryService.saveDiary(me, 30, "test3", LocalDateTime.now());
        DiabetesDiary diary4 = saveDiaryService.saveDiary(me, 40, "test4", LocalDateTime.now());

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()));
        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary3.getId()));

        //when
        Writer found = writerRepository.findAll().get(0);
        List<DiabetesDiary> diaries = diaryRepository.findAll();

        //then
        assertThat(found.getDiaries().size()).isEqualTo(2);
        assertThat(diaries.size()).isEqualTo(2);

        logger.info(found.toString());
        logger.info(found.getDiaries().toString());
        logger.info(diaries.toString());
    }

    @Transactional
    @Test
    public void deleteWriterCascadeDiary() {
        //작성자 삭제되면 일지도 삭제되는 지 cascade 테스트
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        saveDiaryService.saveDiary(me, 10, "test1", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 20, "test2", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 30, "test3", LocalDateTime.now());
        saveDiaryService.saveDiary(me, 40, "test4", LocalDateTime.now());
        writerRepository.delete(me);

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
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);

        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        updateDeleteDiaryService.updateDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()), EatTime.Lunch, 200);

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoSuchElementException("일지 없음"));
        Diet diet = dietRepository.findOneDietByIdInDiary(me.getId(), diary1.getId(), diet1.getDietId())
                .orElseThrow(() -> new NoSuchElementException("식단 없음"));

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
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));

        //when
        DiabetesDiary diary = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoSuchElementException("일지 없음"));
        List<Diet> dietList = dietRepository.findAll();
        //then
        logger.info(diary.getDietList().toString());
        assertThat(diary.getDietList().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
    }

    @Transactional
    @Test
    public void deleteSomeDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);

        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));
        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);

        DiabetesDiary diary2 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 10, 0, 0, 0));
        Diet diet4 = saveDiaryService.saveDiet(me, diary2, EatTime.BreakFast, 120);
        Diet diet5 = saveDiaryService.saveDiet(me, diary2, EatTime.Lunch, 200);
        Diet diet6 = saveDiaryService.saveDiet(me, diary2, EatTime.Dinner, 170);

        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()), EntityId.of(Diet.class, diet1.getDietId()));
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet5.getDietId()));
        updateDeleteDiaryService.deleteDiet(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary2.getId()), EntityId.of(Diet.class, diet6.getDietId()));


        //when
        DiabetesDiary foundDiary1 = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary1.getId())
                .orElseThrow(() -> new NoSuchElementException("일지 없음"));
        DiabetesDiary foundDiary2 = diaryRepository.findOneDiabetesDiaryByIdInWriter(me.getId(), diary2.getId())
                .orElseThrow(() -> new NoSuchElementException("일지 없음"));
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
        assertThat(dietList.size()).isEqualTo(3);
    }

    @Transactional
    @Test
    public void deleteWriterCascadeDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);
        writerRepository.delete(me);

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
    public void deleteDiaryCascadeDiet() {
        //given
        Writer me = saveDiaryService.saveWriter("me", "ME@NAVER.COM", Role.User);
        DiabetesDiary diary1 = saveDiaryService.saveDiary(me, 20, "test1", LocalDateTime.of(2021, 12, 1, 0, 0, 0));

        Diet diet1 = saveDiaryService.saveDiet(me, diary1, EatTime.BreakFast, 100);
        Diet diet2 = saveDiaryService.saveDiet(me, diary1, EatTime.Lunch, 100);
        Diet diet3 = saveDiaryService.saveDiet(me, diary1, EatTime.Dinner, 100);

        updateDeleteDiaryService.deleteDiary(EntityId.of(Writer.class, me.getId()), EntityId.of(DiabetesDiary.class, diary1.getId()));

        //when
        List<Writer>found=writerRepository.findAll();
        List<DiabetesDiary> diaries = diaryRepository.findAll();
        List<Diet> dietList = dietRepository.findAll();

        //then
        logger.info(found.toString());
        logger.info(found.get(0).getDiaries().toString());
        logger.info(dietList.toString());
        logger.info(diaries.toString());
        assertThat(found.get(0).getDiaries().size()).isEqualTo(0);
        assertThat(dietList.size()).isEqualTo(0);
        assertThat(diaries.size()).isEqualTo(0);


    }
}
