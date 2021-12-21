package refactoringAPI.controller.diary.diabetesdiary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiaryListBetweenTimeRequestDTO {

    private final Long writerId;

    //Json LocalDateTime parsing 에러 때문에 String 으로 함.
    private final String startDate;

    private final String endDate;

    public DiaryListBetweenTimeRequestDTO(Long writerId, String startDate, String endDate) {
        this.writerId = writerId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("startDate", startDate)
                .append("endDate", endDate)
                .toString();
    }
}
