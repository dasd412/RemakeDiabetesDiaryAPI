package refactoringAPI.domain.diary.food;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FoodRepository extends JpaRepository<Food, Long> {
     /*
    JPQL 조인은 데이터베이스 테이블의 칼럼을 기준으로 하지 않는다.
    엔티티의 연관 필드를 기준으로 해서 작성해야 한다!!
     */

    @Query(value = "SELECT COUNT(f.foodId) FROM Food f")
    Long findCountOfId();

    @Query(value = "SELECT MAX(f.foodId) FROM Food f")
    Long findMaxOfId();

    //작성자의 혈당 일지 내 식단 내 음식들 조회
    @Query(value = "SELECT food FROM Food food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diary_id AND food.diet.dietId = :diet_id")
    List<Food> findFoodsInDiet(@Param("writer_id") Long writerId, @Param("diary_id") Long diaryId, @Param("diet_id") Long dietId);

    //작성자의 혈당 일지 내 식단 내 특정 음식 조회
    @Query(value = "SELECT food FROM Food food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diary_id AND food.diet.dietId = :diet_id AND food.foodId =:food_id")
    Optional<Food> findOneFoodByIdInDiet(@Param("writer_id") Long writerId, @Param("diary_id") Long diaryId, @Param("diet_id") Long dietId, @Param("food_id") Long foodId);

    //작성자가 먹은 음식 중, 혈당(공복혈당 아님)이 일정 수치인 것들의 음식 이름 조회
    @Query(value="SELECT DISTINCT food.foodName FROM Food food INNER JOIN food.diet diet WHERE diet.bloodSugar >= :blood_sugar AND food.diet.diary.writer.writerId = :writer_id")
    List<String>findFoodNamesInDietHigherThanBloodSugar(@Param("writer_id") Long writerId,@Param("blood_sugar") int bloodSugar);

    //작성자가 먹은 음식 중, 혈당(공복 혈당 아님)이 평균 혈당 이상인 것들의 음식 이름 조회
    @Query(value="SELECT DISTINCT food.foodName FROM Food food INNER JOIN food.diet diet WHERE diet.bloodSugar >= (SELECT AVG(diet.bloodSugar) FROM diet) AND food.diet.diary.writer.writerId = :writer_id")
    List<String>findFoodHigherThanAverageBloodSugarOfDiet(@Param("writer_id") Long writerId);
}
