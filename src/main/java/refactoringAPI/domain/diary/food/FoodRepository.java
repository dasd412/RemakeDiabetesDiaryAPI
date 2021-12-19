package refactoringAPI.domain.diary.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long>,FoodRepositoryCustom {
     /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */
}
