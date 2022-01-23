/*
 * @(#)login.js        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 로그인 폼의 유효성을 검사
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

/**
 *
 * @param username 로그인 폼에서 유저 네임 입력 값
 * @returns {boolean}  공백이 아닌지 판별
 */
function validateId(username) {
    return username.length > 0;
}

/**
 *
 * @param password 로그인 폼에서 비밀 번호 입력 값
 * @returns {boolean} 유효한 비밀 번호인지 판별
 */
function validatePw(password) {
    return password.length >= 7;
}

$(document).ready(function () {
    $("#login-form").on('submit', function (e) {
        e.preventDefault();

        const username = $("#username").val();
        const password = $("#password").val();

        if (validateId(username) === false) {
            swal('', "공백으로 된 이름이에요.", "error");
            return;
        }
        if (validatePw(password) === false) {
            swal('', "비밀번호는 7자 이상이여야 해요.", "error");
            return;
        }

        this.submit();
    });
});