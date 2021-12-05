package jpaEx.domain.diary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiabetesDiaryRepository extends JpaRepository<DiabetesDiary,Long> {

}
