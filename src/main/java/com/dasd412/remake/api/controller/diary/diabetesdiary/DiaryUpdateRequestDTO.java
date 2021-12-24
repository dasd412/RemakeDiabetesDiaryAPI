package com.dasd412.remake.api.controller.diary.diabetesdiary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiaryUpdateRequestDTO {

    private final Long writerId;

    private final Long diaryId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    //cannot deserialize from Object value (no delegate- or property-based Creator) 에러 때문에 기본 생성자 만듬.
    private DiaryUpdateRequestDTO() {
        this.writerId = null;
        this.diaryId = null;
        this.fastingPlasmaGlucose = 0;
        this.remark = null;
    }

    public DiaryUpdateRequestDTO(Long writerId, Long diaryId, int fastingPlasmaGlucose, String remark) {
        this.writerId = writerId;
        this.diaryId = diaryId;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
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

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", diaryId)
                .append("writer", writerId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .toString();
    }
}
