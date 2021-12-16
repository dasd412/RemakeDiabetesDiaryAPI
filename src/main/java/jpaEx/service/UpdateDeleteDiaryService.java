package jpaEx.service;

import jpaEx.domain.diary.EntityId;
import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
import jpaEx.domain.diary.writer.Writer;
import jpaEx.domain.diary.writer.WriterRepository;
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
    private final DiaryRepository diaryRepository;
    private final WriterRepository writerRepository;

    public UpdateDeleteDiaryService(DiaryRepository diaryRepository, WriterRepository writerRepository) {
        this.diaryRepository = diaryRepository;
        this.writerRepository = writerRepository;
    }

    @Transactional
    public DiabetesDiary updateDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId, int fastingPlasmaGlucose, String remark) {
        logger.info("update diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be higher than zero");

        DiabetesDiary targetDiary = diaryRepository.findDiabetesDiaryOfWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));

        targetDiary.update(fastingPlasmaGlucose, remark);

        return targetDiary;
    }

    @Transactional
    public void deleteDiary(EntityId<Writer, Long> writerEntityId, EntityId<DiabetesDiary, Long> diaryEntityId) {
        logger.info("delete diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");

        Writer writer=writerRepository.findById(writerEntityId.getId()).orElseThrow(()->new NoSuchElementException("해당 작성자가 없습니다."));

        DiabetesDiary targetDiary = diaryRepository.findDiabetesDiaryOfWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));
        //연관관계 삭제해야 제대로 삭제됨.
        writer.removeDiary(targetDiary);
        diaryRepository.delete(targetDiary);
    }
}
