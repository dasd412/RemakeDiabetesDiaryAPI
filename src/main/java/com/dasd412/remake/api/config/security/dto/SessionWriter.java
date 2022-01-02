package com.dasd412.remake.api.config.security.dto;

import com.dasd412.remake.api.domain.diary.writer.Writer;

import java.io.Serializable;

public class SessionWriter implements Serializable {

    private final Long writerId;
    private final String name;
    private final String email;

    public SessionWriter(Writer writer) {
        this.writerId = writer.getId();
        this.name = writer.getName();
        this.email = writer.getEmail();
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
}
