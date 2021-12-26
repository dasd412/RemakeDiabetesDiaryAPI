package com.dasd412.remake.api.controller.diary.diabetesdiary;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class DiaryUpdateResponseDTO {

    private final Long diaryId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    public DiaryUpdateResponseDTO(DiabetesDiary diary) {
        this.diaryId = diary.getId();
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        this.remark = diary.getRemark();
        this.writtenTime = diary.getWrittenTime();
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diary", diaryId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }
}
