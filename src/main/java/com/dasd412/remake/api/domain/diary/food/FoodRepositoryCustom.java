package com.dasd412.remake.api.domain.diary.food;

import java.util.List;
import java.util.Optional;

public interface FoodRepositoryCustom {

    Long findMaxOfId();

    List<Food> findFoodsInDiet(Long writerId,Long dietId);

    Optional<Food> findOneFoodByIdInDiet(Long writerId,Long dietId, Long foodId);

    List<String> findFoodNamesInDietHigherThanBloodSugar(Long writerId, int bloodSugar);

    List<String> findFoodHigherThanAverageBloodSugarOfDiet(Long writerId);

    void bulkDeleteFood(List<Long>foodIds);
}
