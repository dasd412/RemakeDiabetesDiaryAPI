package com.dasd412.remake.api.controller.security.domain_view;

import com.dasd412.remake.api.controller.security.domain_rest.dto.chart.FoodBoardDTO;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.InequalitySign;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class FoodPageVOTest {

    private FoodPageVO vo;

    private static final int DEFAULT_SIZE = 10;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() {
        vo = new FoodPageVO();
    }

    @Test
    public void setPage() {
        vo.setPage(10);
        assertThat(vo.getPage()).isEqualTo(10);
    }

    @Test
    public void setPageIfLesserThanZero() {
        vo.setPage(-2);
        assertThat(vo.getPage()).isEqualTo(1);
    }

    @Test
    public void setSize() {
        vo.setSize(25);
        assertThat(vo.getSize()).isEqualTo(25);
    }

    @Test
    public void setSizeIfLesserThanDefault() {
        vo.setSize(8);
        assertThat(vo.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    public void setSizeIfHigherThanDefaultMax() {
        vo.setSize(55);
        assertThat(vo.getSize()).isEqualTo(DEFAULT_SIZE);
    }

    @Test
    public void makePageable() {
        vo.setPage(9);
        vo.setSize(25);

        Pageable page = vo.makePageable();
        assertThat(page.getPageNumber()).isEqualTo(8);
        assertThat(page.getPageSize()).isEqualTo(25);
    }

    @Test
    public void setBloodSugar() {
        vo.setBloodSugar(55);
        assertThat(vo.getBloodSugar()).isEqualTo(55);
    }

    @Test
    public void setMinusBloodSugar() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("blood sugar must be zero or positive");
        vo.setBloodSugar(-23);
    }

    @Test
    public void setSignLesser() {
        vo.setSign("lesser");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.LESSER);
    }

    @Test
    public void setSignGreater() {
        vo.setSign("greater");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.GREATER);
    }

    @Test
    public void setSignEqual() {
        vo.setSign("equal");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.EQUAL);
    }

    @Test
    public void setSignGreatOrEqual() {
        vo.setSign("ge");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.GREAT_OR_EQUAL);
    }

    @Test
    public void setSignLesserOrEqual() {
        vo.setSign("le");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.LESSER_OR_EQUAL);
    }

    @Test
    public void setSignInvalid() {
        vo.setSign("test");
        assertThat(vo.getEnumOfSign()).isEqualTo(InequalitySign.NONE);
    }

    @Test
    public void setDateWithInvalidDateFormat() {
        FoodPageVO vo = FoodPageVO.builder().startYear("test").startMonth("ww").startDay("adsad").build();

        assertThat(vo.convertStartDate()).isEqualTo(Optional.empty());
    }
}