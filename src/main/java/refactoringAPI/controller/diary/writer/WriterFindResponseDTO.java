package refactoringAPI.controller.diary.writer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import refactoringAPI.domain.diary.writer.Role;
import refactoringAPI.domain.diary.writer.Writer;

//todo 스프링 시큐리티 적용 후 변경 예정. 패키지도.
public class WriterFindResponseDTO {

    private final Long writerId;

    private final String name;

    private final String email;

    private final Role role;

    public WriterFindResponseDTO(Writer writer) {
        this.writerId = writer.getId();
        this.name = writer.getName();
        this.email = writer.getEmail();
        this.role = writer.getRole();
    }

    public Long getWriterId() {
        return writerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", writerId)
                .append("name", name)
                .append("email", email)
                .append("role", role)
                .toString();
    }
}
