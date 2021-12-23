package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import java.util.List;
import java.util.Optional;

public class FoodRepositoryImpl implements FoodRepositoryCustom {
    /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
    */

    /*
    부등호 표현식
    lt <
    loe <=
    gt >
    goe >=
    */

    private final JPAQueryFactory jpaQueryFactory;

    public FoodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findCountOfId() {
        return jpaQueryFactory.from(QFood.food).fetchCount();
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QFood.food).select(QFood.food.foodId.max()).fetchOne();
    }

    @Override
    public List<Food> findFoodsInDiet(Long writerId, Long dietId) {
        //@Query(value = "SELECT food FROM Food as food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diaryId AND food.diet.dietId = :diet_id")
        return jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                .fetch();
    }

    @Override
    public Optional<Food> findOneFoodByIdInDiet(Long writerId, Long dietId, Long foodId) {
        //@Query(value = "SELECT food FROM Food as food WHERE food.diet.diary.writer.writerId = :writer_id AND food.diet.diary.diaryId = :diary_id AND food.diet.dietId = :diet_id AND food.foodId =:food_id")
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(QFood.food)
                        .innerJoin(QFood.food.diet, QDiet.diet)
                        .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.dietId.eq(dietId)))
                        .where(QFood.food.foodId.eq(foodId))
                        .fetchOne()
        );
    }

    @Override
    public List<String> findFoodNamesInDietHigherThanBloodSugar(Long writerId, int bloodSugar) {
        //@Query(value="SELECT DISTINCT food.foodName FROM Food as food INNER JOIN food.diet diet WHERE diet.bloodSugar >= :blood_sugar AND food.diet.diary.writer.writerId = :writer_id")
        return jpaQueryFactory.selectDistinct(QFood.food.foodName)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(bloodSugar))
                .fetch();
    }

    @Override
    public List<String> findFoodHigherThanAverageBloodSugarOfDiet(Long writerId) {
        //@Query(value="SELECT DISTINCT food.foodName FROM Food as food INNER JOIN food.diet diet WHERE diet.bloodSugar >= (SELECT AVG(diet.bloodSugar) FROM diet) AND food.diet.diary.writer.writerId = :writer_id")
        return jpaQueryFactory.selectDistinct(QFood.food.foodName)
                .from(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(
                        JPAExpressions.select(QDiet.diet.bloodSugar.avg())
                                .from(QDiet.diet)
                ))
                .fetch();
    }
}
