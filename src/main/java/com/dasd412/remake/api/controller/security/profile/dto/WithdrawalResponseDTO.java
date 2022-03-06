package com.dasd412.remake.api.controller.security.profile.dto;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class WithdrawalResponseDTO {
    private final long writerId;

    public WithdrawalResponseDTO(long writerId) {
        this.writerId = writerId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("writer id", writerId)
                .toString();
    }
}
