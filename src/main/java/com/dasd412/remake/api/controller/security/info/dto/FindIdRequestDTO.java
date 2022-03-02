package com.dasd412.remake.api.controller.security.info.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Email;

@Getter
@RequiredArgsConstructor
public class FindIdRequestDTO {

    @Email
    private final String email;

    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("email", email)
                .toString();
    }
}
