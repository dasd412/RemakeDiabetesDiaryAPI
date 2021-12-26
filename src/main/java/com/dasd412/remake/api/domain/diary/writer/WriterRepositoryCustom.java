package com.dasd412.remake.api.domain.diary.writer;

public interface WriterRepositoryCustom {
    Long findCountOfId();

    Long findMaxOfId();

    void bulkDeleteWriter(Long writerId);
}
