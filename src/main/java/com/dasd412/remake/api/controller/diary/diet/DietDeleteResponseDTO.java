package com.dasd412.remake.api.controller.diary.diet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DietDeleteResponseDTO {

    private final Long writerId;

    private final Long diaryId;

    private final Long dietId;

    public DietDeleteResponseDTO(Long writerId, Long diaryId, Long dietId) {
        this.writerId = writerId;
        this.diaryId = diaryId;
        this.dietId = dietId;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getDietId() { return dietId; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .append("diet", dietId)
                .toString();
    }
}
