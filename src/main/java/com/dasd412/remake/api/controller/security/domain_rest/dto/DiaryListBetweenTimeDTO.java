package com.dasd412.remake.api.controller.security.domain_rest.dto;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;

@Getter
public class DiaryListBetweenTimeDTO {

    private final Long diaryId;

    private final int year;
    private final int month;
    private final int day;

    public DiaryListBetweenTimeDTO(DiabetesDiary diary) {
        this.diaryId = diary.getId();

        LocalDateTime writtenTime = diary.getWrittenTime();
        this.year = writtenTime.getYear();
        this.month = writtenTime.getMonthValue();
        this.day = writtenTime.getDayOfMonth();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("diaryId", diaryId)
                .append("datetime", year + "." + month + "." + day)
                .toString();
    }
}
