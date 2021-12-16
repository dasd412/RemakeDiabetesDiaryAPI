package jpaEx.domain.diary.writer;

import jpaEx.domain.diary.EntityId;
import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
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

        //then
        assertThat(found).isEqualTo(me);
        assertThat(found.getName()).isEqualTo(me.getName());
        assertThat(found.getEmail()).isEqualTo(me.getEmail());
        assertThat(found.getRole()).isEqualTo(me.getRole());
        logger.info(found.toString());

        assertThat(found.getDiaries().get(0).getFastingPlasmaGlucose()).isEqualTo(100);
        assertThat(found.getDiaries().get(0).getRemark()).isEqualTo("modifyTest");
        logger.info(found.getDiaries().get(0).toString());
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

        assertThat(found.getDiaries().size()).isEqualTo(2);
        assertThat(diaries.size()).isEqualTo(2);

        logger.info(found.toString());
        logger.info(found.getDiaries().toString());
        logger.info(diaries.toString());
    }

}
