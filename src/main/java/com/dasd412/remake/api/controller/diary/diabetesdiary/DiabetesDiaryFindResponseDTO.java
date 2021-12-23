package com.dasd412.remake.api.controller.diary.diabetesdiary;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

public class DiabetesDiaryFindResponseDTO {

    private final Long diaryId;

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final LocalDateTime writtenTime;

    public DiabetesDiaryFindResponseDTO(DiabetesDiary diary) {
        this.diaryId = diary.getId();
        this.writerId = diary.getWriter().getId();
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        this.remark = diary.getRemark();
        this.writtenTime = diary.getWrittenTime();
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getWriterId() {
        return writerId;
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
                .append("id", diaryId)
                .append("writer", writerId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }
}
