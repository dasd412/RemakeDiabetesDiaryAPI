package refactoringAPI.controller.diary.writer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import refactoringAPI.domain.diary.writer.Writer;

//todo 스프링 시큐리티 적용 후 변경 예정. 패키지도.
public class WriterFindRequestDTO {

    private final Long writerId;

    public WriterFindRequestDTO(Writer writer) {
        this.writerId = writer.getId();
    }

    public Long getWriterId() {
        return writerId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", writerId)
                .toString();
    }
}
