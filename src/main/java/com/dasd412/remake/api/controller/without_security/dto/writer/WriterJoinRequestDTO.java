package com.dasd412.remake.api.controller.without_security.dto.writer;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.dasd412.remake.api.domain.diary.writer.Role;

public class WriterJoinRequestDTO {

    private final String name;

    private final String email;

    private final Role role;

    public WriterJoinRequestDTO(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
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
                .append("name", name)
                .append("email", email)
                .append("role", role)
                .toString();
    }
}
