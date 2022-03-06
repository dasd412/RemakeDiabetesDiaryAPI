package com.dasd412.remake.api.controller.security.join;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class UserJoinRequestDTO implements Serializable {

    @NotNull
    @NotEmpty
    private final String name;

    @NotNull
    @NotEmpty
    private final String password;

    @NotNull
    @NotEmpty
    @Email
    private final String email;

    public UserJoinRequestDTO(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("name", name)
                .append("email", email)
                .toString();
    }
}
