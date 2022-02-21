/*
 * @(#)login.js        1.1.0 2022/2/21
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 로그인 폼의 유효성을 검사
 *
 * @author 양영준
 * @version 1.1.0 2022년 2월 21일
 */

$(document).ready(function () {
    $("#login-form").on('submit', function (e) {
        e.preventDefault();

        const username = $("#username").val();
        const password = $("#password").val();

        const loginIdValidate = InfoValidator.validateId(username);
        if (loginIdValidate.boolean === false) {
            swal('', "아이디의 길이는" + loginIdValidate.minLength + "이상" + loginIdValidate.maxLENGTH + "이여야 합니다.", "error");
            return;
        }

        const loginPasswordValidate = InfoValidator.validatePassword(password);
        if (loginPasswordValidate.boolean === false) {
            swal('', "비밀번호는" + loginPasswordValidate.minLength + "자 이상" + loginPasswordValidate.maxLength + "글자 이하여야 합니다.", "error");
            return;
        }

        this.submit();
    });
});