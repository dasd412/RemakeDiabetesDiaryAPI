package com.dasd412.remake.api.domain.diary.diet;

import com.dasd412.remake.api.domain.diary.EntityId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Profile;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import com.dasd412.remake.api.domain.diary.food.Food;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations="classpath:application-test.properties")
public class DietTest {
    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer writer;
    DiabetesDiary diary;
    Diet diet;

    @Before
    public void setUp() {
        writer = new Writer(EntityId.of(Writer.class, 1L), "me", "test@naver.com", Role.Admin);
        diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        diet = new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.Lunch, 110);
        diary.addDiet(diet);
    }

    @Test
    public void makeRelationWithDiary() {
        assertThat(diet.getDiary()).isEqualTo(diary);
    }

    @Test
    public void createInvalidBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 0 and 1000");
        new Diet(EntityId.of(Diet.class, 1L), diary, EatTime.Lunch, 15000);
    }

    @Test
    public void update() {
        diet.update(EatTime.Dinner, 150);
        assertThat(diet.getDietId()).isEqualTo(1L);
        assertThat(diet.getBloodSugar()).isEqualTo(150);
        assertThat(diet.getEatTime()).isEqualTo(EatTime.Dinner);
    }

    @Test
    public void updateInvalidBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("bloodSugar must be between 0 and 1000");
        diet.update(EatTime.Dinner, -1);
    }

    @Test
    public void addFood() {
        Food food = new Food(EntityId.of(Food.class, 1L), diet, "cola");
        diet.addFood(food);
        assertThat(diet.getFoodList().contains(food)).isTrue();
    }

    @Test
    public void removeFood() {
        Food food = new Food(EntityId.of(Food.class, 1L), diet, "cola");
        diet.addFood(food);
        diet.removeFood(food);
        assertThat(diet.getFoodList().contains(food)).isFalse();
    }

    @Test
    public void removeFoodAlreadyRemoved() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("this diet dose not have the food");

        Food food = new Food(EntityId.of(Food.class, 1L), diet, "cola");
        diet.addFood(food);
        diet.removeFood(food);
        diet.removeFood(food);
        assertThat(diet.getFoodList().contains(food)).isFalse();
    }

}