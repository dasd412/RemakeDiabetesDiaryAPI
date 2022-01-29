/*
 * @(#)DiabetesDiaryTest.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */


package com.dasd412.remake.api.domain.diary.diabetesDiary;

import com.dasd412.remake.api.domain.diary.EntityId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Profile;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jpa 사용하지 않고 DiabetesDiary 메서드 테스트.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@TestPropertySource(locations="classpath:application-test.properties")
public class DiabetesDiaryTest {
    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer writer;
    DiabetesDiary diary;

    @Before
    public void setUp() {
        writer = new Writer(EntityId.of(Writer.class, 1L), "me", "test@naver.com", Role.Admin);
        diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
    }

    @Test
    public void makeRelationWithWriter() {
        assertThat(diary.getWriter()).isEqualTo(writer);
    }

    @Test
    public void createInvalidBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 0 and 1000");
        DiabetesDiary invalid = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 2L), writer, 10000, "test", LocalDateTime.now());
    }

    @Test
    public void createInvalidRemark() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("remark length should be lower than 501");

        StringBuilder remark = new StringBuilder();
        IntStream.range(0, 1000).forEach(i -> remark.append("a"));
        DiabetesDiary invalid = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 2L), writer, 100, remark.toString(), LocalDateTime.now());
    }

    @Test
    public void update() {
        diary.update(200,"update");
        assertThat(diary.getId()).isEqualTo(1L);
        assertThat(diary.getWriter()).isEqualTo(writer);
        assertThat(diary.getFastingPlasmaGlucose()).isEqualTo(200);
        assertThat(diary.getRemark()).isEqualTo("update");
    }

    @Test
    public void updateInvalidBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("fastingPlasmaGlucose must be between 0 and 1000");
        diary.update(-2000,"");
    }

    @Test
    public void updateInvalidRemark() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("remark length should be lower than 501");
        StringBuilder remark = new StringBuilder();
        IntStream.range(0, 1000).forEach(i -> remark.append("a"));
        diary.update(100,remark.toString());
    }

    @Test
    public void addDiet(){
        Diet diet=new Diet(EntityId.of(Diet.class,1L),diary, EatTime.Lunch,100);
        diary.addDiet(diet);
        assertThat(diary.getDietList().contains(diet)).isTrue();
    }

    @Test
    public void removeDiet(){
        Diet diet=new Diet(EntityId.of(Diet.class,1L),diary, EatTime.Lunch,100);
        diary.addDiet(diet);
        diary.removeDiet(diet);
        assertThat(diary.getDietList().contains(diet)).isFalse();
    }

    @Test
    public void removeDietAlreadyRemoved(){
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("this diary dose not have the diet");
        Diet diet=new Diet(EntityId.of(Diet.class,1L),diary, EatTime.Lunch,100);
        diary.addDiet(diet);
        diary.removeDiet(diet);
        diary.removeDiet(diet);
    }
}