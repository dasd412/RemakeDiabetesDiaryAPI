package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.EntityId;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.context.annotation.Profile;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(locations="classpath:application-test.properties")
public class WriterTest {
    //예외 캐치용 객체
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer writer;

    @Before
    public void setUp() {
        writer = new Writer(EntityId.of(Writer.class, 1L), "me", "test@naver.com", Role.Admin);
    }

    @Test
    public void createWriterEmptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        Writer emptyName = new Writer(EntityId.of(Writer.class, 1L), "", "test@naver.com", Role.Admin);
    }

    @Test
    public void createWriterLongName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        Writer longName = new Writer(EntityId.of(Writer.class, 1L), "123456789012345678901234567890123456789012345678901234567890", "test@naver.com", Role.Admin);
    }

    @Test
    public void update() {

        writer.update("other", "other@naver.com", Role.User);

        assertThat(writer.getId()).isEqualTo(1L);
        assertThat(writer.getName()).isEqualTo("other");
        assertThat(writer.getEmail()).isEqualTo("other@naver.com");
        assertThat(writer.getRole()).isEqualTo(Role.User);
    }

    @Test
    public void updateEmptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        writer.update("", "other@naver.com", Role.User);
    }

    @Test
    public void updateLongName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");
        writer.update("123456789012345678901234567890123456789012345678901234567890", "other@naver.com", Role.User);
    }

    @Test
    public void addDiary() {
        DiabetesDiary diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        assertThat(writer.getDiaries().contains(diary)).isTrue();
    }

    @Test
    public void removeDiary() {
        DiabetesDiary diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        writer.removeDiary(diary);
        assertThat(writer.getDiaries().contains(diary)).isFalse();
    }

    @Test
    public void removeDiaryAlreadyRemoved() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("this writer does not have the diary");
        DiabetesDiary diary = new DiabetesDiary(EntityId.of(DiabetesDiary.class, 1L), writer, 100, "test", LocalDateTime.now());
        writer.addDiary(diary);
        writer.removeDiary(diary);
        writer.removeDiary(diary);
    }


}