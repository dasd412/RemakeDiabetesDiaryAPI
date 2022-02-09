/*
 * @(#)FoodPageVO.java        1.0.7 2022/2/9
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.food;

import lombok.Getter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 음식 게시판의 조회 페이징을 위해 사용되는 VO
 *
 * @author 양영준
 * @version 1.0.7 2022년 2월 9일
 */
@Getter
public class FoodPageVO {

    private static final int DEFAULT_SIZE = 10;
    private static final int DEFAULT_MAX_SIZE = 50;

    /**
     * 페이지 번호 (브라우저에서 전달되는 값은 1이다.)
     * 그런데 Pageble 객체의 offset = page * size이기 때문에 pageable 객체를 만들 때는 page-1 해준다.
     */
    private int page;

    /**
     * 음식 이름의 수
     */
    private int size;

    public FoodPageVO() {
        this.page = 1;
        this.size = DEFAULT_SIZE;
    }

    public void setPage(int page) {
        this.page = page < 0 ? 1 : page;
    }

    /**
     * 만약, 음식 이름 게시물의 수가 기본 10개보다 적거나, 50개를 초과할 경우에는 기본 사이즈인 10으로 정해준다.
     *
     * @param size 음식 이름 게시물의 수
     */
    public void setSize(int size) {
        this.size = size < DEFAULT_SIZE || size > DEFAULT_MAX_SIZE ? DEFAULT_SIZE : size;
    }

    /**
     * Pageble 객체의 offset = page * size이기 때문에 pageable 객체를 만들 때는 page-1 해준다.
     * @param direction  정렬 방향
     * @param properties 정렬 기준
     * @return  Pageable 객체.
     */
    public Pageable makePageable(Sort.Direction direction, String... properties) {
        return PageRequest.of(this.page - 1, this.size, direction, properties);
    }

}
