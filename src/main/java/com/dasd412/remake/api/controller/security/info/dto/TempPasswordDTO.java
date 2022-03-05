package com.dasd412.remake.api.controller.security.info.dto;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class TempPasswordDTO {
    private final String tempPassword;

    public TempPasswordDTO(String tempPassword) {
        this.tempPassword = tempPassword;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("temp password", "")
                .toString();
    }
}
