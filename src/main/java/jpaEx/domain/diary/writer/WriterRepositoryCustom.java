package jpaEx.domain.diary.writer;

import org.springframework.data.jpa.repository.Query;

public interface WriterRepositoryCustom {
    Long findCountOfId();

    Long findMaxOfId();
}
