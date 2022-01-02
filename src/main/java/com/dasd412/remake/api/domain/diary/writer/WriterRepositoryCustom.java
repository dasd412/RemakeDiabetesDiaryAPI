package com.dasd412.remake.api.domain.diary.writer;

import java.util.Optional;

public interface WriterRepositoryCustom {
    Long findCountOfId();

    Long findMaxOfId();

    void bulkDeleteWriter(Long writerId);

    Optional<Writer> findWriterByName(String name);

    Boolean existsName(String name);

    Boolean existsEmail(String email, String provider);

}
