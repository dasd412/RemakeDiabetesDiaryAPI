package refactoringAPI.controller.diary.diet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import refactoringAPI.domain.diary.diet.EatTime;

public class DietRequestDTO {

    private final Long writerId;

    private final Long diaryId;

    private final EatTime eatTime;

    private final int bloodSugar;

    public DietRequestDTO(Long writerId, Long diaryId, EatTime eatTime, int bloodSugar) {
        this.writerId = writerId;
        this.diaryId = diaryId;
        this.eatTime = eatTime;
        this.bloodSugar = bloodSugar;
    }

    public Long getWriterId() {
        return writerId;
    }

    public Long getDiaryId() {
        return diaryId;
    }

    public EatTime getEatTime() {
        return eatTime;
    }

    public int getBloodSugar() {
        return bloodSugar;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer", writerId)
                .append("diary", diaryId)
                .append("bloodSugar", bloodSugar)
                .append("eatTime", eatTime)
                .toString();
    }
}
