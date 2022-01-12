package com.dasd412.remake.api.controller.security.domain_rest.dto;

public class SecurityDiaryUpdateResponseDTO {

    private final Long diaryId;

    public SecurityDiaryUpdateResponseDTO(Long diaryId) {
        this.diaryId = diaryId;
    }

    public Long getDiaryId() {
        return diaryId;
    }
}
