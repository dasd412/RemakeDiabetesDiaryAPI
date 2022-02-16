package com.dasd412.remake.api.controller.without_security.dto.diabetesdiary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiaryDeleteResponseDTO {

    private final Long writerId;

    private final Long diaryId;

    public DiaryDeleteResponseDTO(Long writerId, Long diaryId) {
        this.writerId = writerId;
        this.diaryId = diaryId;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public Long getWriterId() {
        return writerId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .toString();
    }
}
