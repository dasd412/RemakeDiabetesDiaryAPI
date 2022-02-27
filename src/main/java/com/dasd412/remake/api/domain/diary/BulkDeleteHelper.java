/*
 * @(#)BulkDeleteHelper.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary;

import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diabetesDiary.QDiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.QDiet;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.food.QFood;
import com.dasd412.remake.api.domain.diary.profile.QProfile;
import com.dasd412.remake.api.domain.diary.writer.QWriter;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Querydsl bulk delete 시 중복 코드를 제거하기 위해 만든 리팩토링용 클래스
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 27일
 */
public class BulkDeleteHelper {

    private final JPAQueryFactory jpaQueryFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public BulkDeleteHelper(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private Long getProfileId(Long writerId) {
        return jpaQueryFactory.select(QWriter.writer.profile.profileId)
                .from(QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .fetchOne();
    }

    private List<Long> getDiaryIds(Long writerId) {
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary)
                .innerJoin(QDiabetesDiary.diabetesDiary.writer, QWriter.writer)
                .on(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId))
                .fetch()
                .stream().map(
                        DiabetesDiary::getId
                ).collect(Collectors.toList());
    }

    private List<Long> getDietIds(List<Long> diaryIdList) {
        return jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.diaryId.in(diaryIdList))
                .fetch()
                .stream().map(
                        Diet::getDietId
                ).collect(Collectors.toList());
    }

    private List<Long> getFoodIds(List<Long> dietIdList) {
        return jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.dietId.in(dietIdList))
                .fetch()
                .stream().map(
                        Food::getId
                ).collect(Collectors.toList());
    }

    private void deleteProfile(Long profileId) {
        jpaQueryFactory.delete(QProfile.profile)
                .where(QProfile.profile.profileId.eq(profileId))
                .execute();
    }

    private void deleteFoodsInIds(List<Long> foodIdList) {
        jpaQueryFactory.delete(QFood.food)
                .where(QFood.food.foodId.in(foodIdList))
                .execute();
    }

    private void deleteDietInIds(List<Long> dietIdList) {
        jpaQueryFactory.delete(QDiet.diet)
                .where(QDiet.diet.dietId.in(dietIdList))
                .execute();
    }

    private void deleteDiaryInIds(List<Long> diaryIdList) {
        jpaQueryFactory.delete(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.diaryId.in(diaryIdList))
                .execute();
    }

    public void bulkDeleteWriter(Long writerId) {
        logger.info("bulk delete writer and related sub entities");

        /* select profile id */
        Long profileId = getProfileId(writerId);

        /* select diary ids */
        List<Long> diaryIdList = getDiaryIds(writerId);

        /* select diet ids */
        List<Long> dietIdList = getDietIds(diaryIdList);

        /* select food ids */
        List<Long> foodIdList = getFoodIds(dietIdList);


        /* bulk delete food */
        deleteFoodsInIds(foodIdList);

        /* bulk delete diet */
        deleteDietInIds(dietIdList);

        /* bulk delete diary*/
        deleteDiaryInIds(diaryIdList);

        /* delete writer */
        jpaQueryFactory.delete(QWriter.writer)
                .where(QWriter.writer.writerId.eq(writerId))
                .execute();

        /* delete profile */
        if (profileId != null) {
            deleteProfile(profileId);
        }
    }


    public void bulkDeleteDiary(Long diaryId) {
        logger.info("bulk delete diary and related sub entities");
        /* select diet id */
        List<Long> dietIdList = jpaQueryFactory.selectFrom(QDiet.diet)
                .innerJoin(QDiet.diet.diary, QDiabetesDiary.diabetesDiary)
                .on(QDiet.diet.diary.diaryId.eq(diaryId))
                .fetch()
                .stream().map(
                        Diet::getDietId
                ).collect(Collectors.toList());

        /* select food id */
        List<Long> foodIdList = getFoodIds(dietIdList);

        /* bulk delete food */
        deleteFoodsInIds(foodIdList);

        /* bulk delete diet */
        deleteDietInIds(dietIdList);

        jpaQueryFactory.delete(QDiabetesDiary.diabetesDiary)
                .where(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId))
                .execute();
    }

    public void bulkDeleteDiet(Long dietId) {
        logger.info("bulk delete diet and related sub entities");
        /* select food id */
        List<Long> foodIdList = jpaQueryFactory.selectFrom(QFood.food)
                .innerJoin(QFood.food.diet, QDiet.diet)
                .on(QDiet.diet.dietId.eq(dietId))
                .fetch()
                .stream().map(
                        Food::getId
                ).collect(Collectors.toList());

        /* bulk delete food */
        deleteFoodsInIds(foodIdList);

        /* bulk delete diet */
        jpaQueryFactory.delete(QDiet.diet)
                .where(QDiet.diet.dietId.eq(dietId))
                .execute();
    }

}
