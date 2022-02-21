/*
 * @(#)InfoValidator.js        1.1.0 2022/2/21
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 로그인 또는 회원 가입 시 필요한 정보들의 유효성을 검사
 *
 * @author 양영준
 * @version 1.1.0 2022년 2월 21일
 */

/**
 * 로그인 , 회원 가입 시 필요한 정보들이 적절한 값인지 검증해주는 객체
 * @type {{initConst: InfoValidator.initConst, validatePassword: (function(*)), validateId: (function(*)), validateEmail: (function(*): boolean)}}
 */
const InfoValidator = {

    /**
     * 유효성 검증에 필요한 상숫값 초기화.
     */
    initConst: function () {
        this.MIN_ID_LENGTH = 4;
        this.MAX_ID_LENGTH = 15;

        this.MIN_PW_LENGTH = 8;
        this.MAX_PW_LENGTH = 20;
    },

    /**
     *
     * @param str 이메일 문자열
     * @returns  이메일 정규식에 맞는 지 판별
     */

    validateEmail: function (str) {
        const regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
        return regExp.test(str);
    },

    /**
     * @param id 아이디 입력 값
     * @returns  적합한 id 형식인지 판별
     */
    validateId: function (id) {

        const idValidate = () => {
            const boolean = id.length >= this.MIN_ID_LENGTH && id.length <= this.MAX_ID_LENGTH;
            const minLength = this.MIN_ID_LENGTH;
            const maxLENGTH = this.MAX_ID_LENGTH;

            return {boolean, minLength, maxLENGTH};
        };

        return idValidate();
    },

    /**
     * @param password 비밀 번호 입력 값
     * @returns 유효한 비밀 번호인지 판별
     */
    validatePassword: function (password) {

        const passwordValidate = () => {
            const boolean = password.length >= this.MIN_PW_LENGTH && password.length <= this.MAX_PW_LENGTH;
            const minLength = this.MIN_PW_LENGTH;
            const maxLength = this.MAX_PW_LENGTH;

            return {boolean, minLength, maxLength};
        };

        return passwordValidate();
    }

};

$(document).ready(function () {
    InfoValidator.initConst();
});