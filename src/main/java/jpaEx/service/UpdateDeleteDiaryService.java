package jpaEx.service;

import jpaEx.domain.diary.EntityId;
import jpaEx.domain.diary.diabetesDiary.DiabetesDiary;
import jpaEx.domain.diary.diabetesDiary.DiaryRepository;
import jpaEx.domain.diary.writer.Writer;
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

    public UpdateDeleteDiaryService(DiaryRepository diaryRepository) {
        this.diaryRepository = diaryRepository;
    }

    @Transactional
    public DiabetesDiary update(EntityId<Writer> writerEntityId, EntityId<DiabetesDiary> diaryEntityId, int fastingPlasmaGlucose, String remark) {
        logger.info("update diary");

        checkNotNull(writerEntityId, "writerId must be provided");
        checkNotNull(diaryEntityId, "diaryId must be provided");
        checkArgument(fastingPlasmaGlucose > 0, "fpg must be higher than zero");

        DiabetesDiary targetDiary = diaryRepository.findDiabetesDiaryOfWriter(writerEntityId.getId(), diaryEntityId.getId())
                .orElseThrow(() -> new NoSuchElementException("해당 혈당일지가 존재하지 않습니다."));

        targetDiary.update(fastingPlasmaGlucose, remark);

        return targetDiary;
    }


}
