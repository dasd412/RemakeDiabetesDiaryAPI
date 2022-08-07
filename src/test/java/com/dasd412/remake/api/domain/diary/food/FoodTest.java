package com.dasd412.remake.api.domain.diary.food;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.diet.EatTime;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.assertj.core.data.Offset;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FoodTest {
    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer writer;
    DiabetesDiary diary;
    Diet diet;
    Food food;

    @Before
    public void setUp() {
        writer = Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("me")
                .email("me@naver.com")
                .password("password")
                .role(Role.Admin)
                .provider("provider")
                .providerId("1")
                .build();

        diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        diet = new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.Lunch, 110);
        diary.addDiet(diet);
        food = new Food(EntityId.of(Food.class, 1L), diet, "Pizza");
        diet.addFood(food);
    }

    @Test
    public void makeRelationWithDiet() {
        assertThat(food.getDiet()).isEqualTo(diet);
    }

    @Test
    public void createInvalidFoodName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        StringBuilder foodName = new StringBuilder();
        IntStream.range(0, 100).forEach(i -> foodName.append("a"));
        new Food(EntityId.of(Food.class, 2L), diet, foodName.toString());
    }

    @Test
    public void convertNullAmountUnit() {
        assertThat(food.getAmountUnit()).isEqualTo(AmountUnit.NONE);
    }

    @Test
    public void updateOnlyFoodName() {
        food.update("chicken");
        assertThat(food.getId()).isEqualTo(1L);
        assertThat(food.getFoodName()).isEqualTo("chicken");
    }

    @Test
    public void updateFoodNameAndAmountAndAmountUnit() {
        food.update("chicken", 1.5, AmountUnit.kg);
        assertThat(food.getId()).isEqualTo(1L);
        assertThat(food.getFoodName()).isEqualTo("chicken");
        assertThat(food.getAmount()).isCloseTo(1.5, Offset.offset(0.05));
        assertThat(food.getAmountUnit()).isEqualTo(AmountUnit.kg);
    }


    @Test
    public void updateInvalidFoodName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("food name length should be between 1 and 50");
        food.update("");
    }

    @Test
    public void testEqualsTrue() {
        assertThat(food.equals(food)).isTrue();

        Food sameId = new Food(EntityId.of(Food.class, 1L), diet, "Pizza");
        assertThat(food.equals(sameId)).isTrue();
    }

}