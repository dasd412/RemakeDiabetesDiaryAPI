package com.dasd412.remake.api.service.domain.vo;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Objects;

public class FromStartUntilEnd {

    private final LocalDateTime startDate;
    private final LocalDateTime endDate;

    @Builder
    private FromStartUntilEnd(LocalDateTime startDate, LocalDateTime endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startDate, endDate);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        FromStartUntilEnd target = (FromStartUntilEnd) obj;
        return Objects.equals(this.startDate, target.startDate)
                && Objects.equals(this.endDate, target.endDate);
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }
}
