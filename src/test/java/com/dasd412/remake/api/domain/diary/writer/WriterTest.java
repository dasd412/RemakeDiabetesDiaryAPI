package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class WriterTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    Writer writer;

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
    }

    @Test
    public void createWriterEmptyName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");

        Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("")
                .email("test@naver.com")
                .password("password")
                .role(Role.Admin)
                .provider("provider")
                .providerId("2")
                .build();
    }

    @Test
    public void createWriterLongName() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("name should be between 1 and 50");

        Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("123456789012345678901234567890123456789012345678901234567890")
                .email("test@naver.com")
                .password("password")
                .role(Role.Admin)
                .provider("provider")
                .providerId("2")
                .build();
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

    @Test
    public void testEquals(){
        assertThat(writer.equals(writer)).isTrue();

        Writer sameId=Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name("me")
                .email("me@naver.com")
                .password("password")
                .role(Role.Admin)
                .provider("provider")
                .providerId("1")
                .build();
        assertThat(writer.equals(sameId)).isTrue();
    }
}