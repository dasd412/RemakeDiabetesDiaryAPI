package com.dasd412.remake.api.domain.diary.diet;


import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.QFood;
import com.dasd412.remake.api.domain.diary.writer.QWriter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DietRepositoryImpl implements DietRepositoryCustom {
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

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.dietId.max()).fetchOne();
    }

    @Override
    public List<Diet> findDietsInDiary(Long writerId, Long diaryId) {
        //@Query(value = "SELECT diet FROM Diet as diet WHERE diet.diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id")
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.diary.diaryId.eq(diaryId)))
                .fetch();
    }

    @Override
    public Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long dietId) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id AND  diet.dietId = :diet_id")
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId).and(QDiet.diet.diary.diaryId.eq(diaryId)))
                .where(QDiet.diet.dietId.eq(dietId))
                .fetchOne());
    }

    @Override
    public List<Diet> findHigherThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diary.writer.writerId = :writer_id AND diet.bloodSugar >= :blood_sugar AND diet.diary.writtenTime BETWEEN :startDate AND :endDate")
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(bloodSugar).and(QDiet.diet.diary.writtenTime.between(startDate, endDate)))
                .fetch();
    }

    @Override
    public List<Diet> findLowerThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate) {
        //Hibernate: select diet0_.diary_id as diary_id0_1_, diet0_.writer_id as writer_i0_1_, diet0_.diet_id as diet_id1_1_, diet0_.diary_id as diary_id4_1_, diet0_.writer_id as writer_i5_1_, diet0_.blood_sugar as blood_su2_1_, diet0_.eat_time as eat_time3_1_ from diet diet0_ inner join diabetes_diary diabetes di1_ on diet0_.diary_id=diabetes di1_.diary_id and diet0_.writer_id=diabetes di1_.writer_id and (diabetes di1_.writer_id=?) where diet0_.blood_sugar<=? and (diabetes di1_.written_time between ? and ?)
        //@Query(value = "SELECT diet FROM Diet as diet WHERE diary.writer.writerId = :writer_id AND diet.bloodSugar <= :blood_sugar AND diet.diary.writtenTime BETWEEN :startDate AND :endDate")
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.loe(bloodSugar).and(QDiet.diet.diary.writtenTime.between(startDate, endDate)))
                .fetch();
    }

    @Override
    public List<Diet> findHigherThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime) {
        //@Query(value = "SELECT diet FROM Diet as diet WHERE diary.writer.writerId = :writer_id AND diet.bloodSugar >= :blood_sugar AND diet.eatTime =:eat_time")
        //Hibernate: select diet0_.diary_id as diary_id0_1_, diet0_.writer_id as writer_i0_1_, diet0_.diet_id as diet_id1_1_, diet0_.diary_id as diary_id4_1_, diet0_.writer_id as writer_i5_1_, diet0_.blood_sugar as blood_su2_1_, diet0_.eat_time as eat_time3_1_ from diet diet0_ inner join diabetes_diary diabetes di1_ on diet0_.diary_id=diabetes di1_.diary_id and diet0_.writer_id=diabetes di1_.writer_id and (diabetes di1_.writer_id=?) inner join writer writer2_ on diabetes di1_.writer_id=writer2_.writer_id where diet0_.blood_sugar>=? and diet0_.eat_time=?
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.goe(bloodSugar).and(QDiet.diet.eatTime.eq(eatTime)))
                .fetch();
    }

    @Override
    public List<Diet> findLowerThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diary.writer.writerId = :writer_id AND diet.bloodSugar <= :blood_sugar AND diet.eatTime =:eat_time")
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .where(QDiet.diet.bloodSugar.loe(bloodSugar).and(QDiet.diet.eatTime.eq(eatTime)))
                .fetch();
    }

    @Override
    public Optional<Double> findAverageBloodSugarOfDiet(Long writerId) {
        //@Query(value="SELECT AVG(diet.bloodSugar) FROM Diet as diet WHERE diet.diary.writer.writerId = :writer_id")
        //jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.bloodSugar.avg()).where(QDiet.diet.diary.writer.writerId.eq(writerId)).fetchOne()); <-크로스 조인 발생하는 코드
        return Optional.ofNullable(jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.bloodSugar.avg())
                .innerJoin(QDiet.diet.diary.writer, QWriter.writer)
                .on(QDiet.diet.diary.writer.writerId.eq(writerId))
                .fetchOne());
    }

    @Override
    public void bulkDeleteDiet(Long dietId) {
        //select food id
        logger.info("select food id");
        List<Long> foodIdList = jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.dietId.eq(dietId))
                .fetch()
                .stream().map(
                        Food::getId
                ).collect(Collectors.toList());

        //bulk delete food
        logger.info("bulk delete food");
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.foodId.in(foodIdList))
                .execute();

        //bulk delete diet
        logger.info("bulk delete diet");
        jpaQueryFactory.delete(QDiet.diet)
                .where(QDiet.diet.dietId.eq(dietId))
                .execute();
    }
}
