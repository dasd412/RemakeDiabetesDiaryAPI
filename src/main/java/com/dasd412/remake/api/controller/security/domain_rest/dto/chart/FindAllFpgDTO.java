package com.dasd412.remake.api.controller.security.domain_rest.dto.chart;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class FindAllFpgDTO {

    /**
     * y - 축
     */
    private final int fastingPlasmaGlucose;

    /**
     * x -축
     * 1970/01/01 00:00:00 GMT 부터 millisecond로 계산한 시간 출력
     */
    private final long timeByTimeStamp;

    public FindAllFpgDTO(DiabetesDiary diary) {
        this.fastingPlasmaGlucose = diary.getFastingPlasmaGlucose();
        LocalDateTime localDateTime = diary.getWrittenTime();
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        this.timeByTimeStamp = timestamp.getTime();
    }

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("fpg", this.fastingPlasmaGlucose)
                .append("timeStamp", this.timeByTimeStamp)
                .toString();
    }
}
