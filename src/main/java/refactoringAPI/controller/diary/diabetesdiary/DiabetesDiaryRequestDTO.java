package refactoringAPI.controller.diary.diabetesdiary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiabetesDiaryRequestDTO {

    private final Long writerId;

    private final int fastingPlasmaGlucose;

    private final String remark;

    private final String year;

    private final String month;

    private final String day;

    private final String hour;

    private final String minute;

    private final String second;

    public DiabetesDiaryRequestDTO(Long writerId, int fastingPlasmaGlucose, String remark, String year, String month, String day, String hour, String minute, String second) {
        this.writerId = writerId;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
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

    public String getYear() {
        return year;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public String getMinute() {
        return minute;
    }

    public String getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", year + " : " + month + " : " + day + " : " + hour + " : " + minute + " : " + second)
                .toString();
    }
}
