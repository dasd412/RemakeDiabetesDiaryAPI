package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.QFood;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WriterRepositoryImpl implements WriterRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;
    /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
     */
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public WriterRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findCountOfId() {
        return jpaQueryFactory.from(QWriter.writer).fetchCount();
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QWriter.writer).select(QWriter.writer.writerId.max()).fetchOne();
    }

    @Override
    public void bulkDeleteWriter(Long writerId) {
        //select diary ids
        logger.info("select diary id");
        List<Long> diaryIdList = jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .on(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId))
                .fetch()
                .stream().map(
                        DiabetesDiary::getId
                ).collect(Collectors.toList());

        //select diet ids
        logger.info("select diet ids");
        List<Long> dietIdList = jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.diaryId.in(diaryIdList))
                .fetch()
                .stream().map(
                        Diet::getDietId
                ).collect(Collectors.toList());

        //select food ids
        logger.info("select food ids");
        List<Long> foodIdList = jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.dietId.in(dietIdList))
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
                .where(QDiet.diet.dietId.in(dietIdList))
                .execute();

        logger.info("bulk delete diary");
        jpaQueryFactory.delete(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.diaryId.in(diaryIdList))
                .execute();

        //delete writer
        logger.info("delete writer");
        jpaQueryFactory.delete(QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .execute();
    }

    @Override
    public Optional<Writer> findWriterByName(String name) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QWriter.writer).where(QWriter.writer.name.eq(name)).fetchOne());
    }
}
