package com.dasd412.remake.api.controller.security.profile.dto;

import com.dasd412.remake.api.controller.security.PasswordSize;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Size;

@Getter
public class PasswordUpdateRequestDTO {

    @Size(min = PasswordSize.MIN_SIZE, max = PasswordSize.MAX_SIZE)
    private final String password;

    @Size(min = PasswordSize.MIN_SIZE, max = PasswordSize.MAX_SIZE)
    private final String passwordConfirm;

    @Builder
    public PasswordUpdateRequestDTO(String password, String passwordConfirm) {
        this.password = password;
        this.passwordConfirm = passwordConfirm;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("password", "")
                .append("passwordConfirm", "")
                .toString();
    }
}
