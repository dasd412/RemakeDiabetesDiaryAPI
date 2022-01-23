/*
 * @(#)signup.js        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 회원 가입 폼의 유효성을 검사
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */

/**
 *
 * @param str 이메일 문자열
 * @returns {boolean} 이메일 정규식에 맞는 지 판별
 */
function validateEmail(str) {
    const regExp = /^[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_\.]?[0-9a-zA-Z])*\.[a-zA-Z]{2,3}$/i;
    return regExp.test(str);
}

/**
 * 이벤트 : 회원 가입 버튼 클릭
 * 로직 : 유효성 검증 -> 회원 가입 POST 요청
 */
function submitSignup() {
    const username = $("#username").val();
    const password = $("#password").val();
    const email = $("#email").val();

    if (username == null || username === "") {
        swal('', "공백으로 된 이름은 만들 수 없어요.", "error");
        return;
    }
    if (password == null || password === "") {
        swal('', "공백으로 된 패스워드는 만들 수 없어요.", "error");
        return;
    }
    if (password.length <= 6) {
        swal('', "비밀번호는 7자 이상이여야 해요.", "error");
        return;
    }
    if (email == null || email === "") {
        swal('', "공백으로 된 이메일은 만들 수 없어요.", "error");
        return;
    }
    if (validateEmail(email) === false) {
        swal('', "올바른 형식의 이메일이 아니에요.", "error");
        return;
    }


    const userData = {
        name: username,
        password: password,
        email: email
    };

    $.ajax({
        type: "POST",
        url: "/signup/user",
        dataType: 'json',
        contentType: 'application/json; charset=utf-8',
        data: JSON.stringify(userData)
    }).done(function (e) {

        //회원가입 성공 시 처리
        if (e.success === true) {
            swal('', "회원 가입 성공!", "success");
            //실패 시 alert message
        } else {
            if (e.error.message === "duplicateName") {
                swal('', "중복된 이름이에요.", "error");
            } else if (e.error.message === "duplicateEmail") {
                swal('', "중복된 이메일이에요.", "error");
            }
        }

    });
}

/**
 * 로그인 폼으로 돌아가기
 */
function goBackLogin() {
    window.location.href = "/loginForm";
}

$(document).ready(function () {
    $('#signupBtn').attr('onclick', 'submitSignup()');
    $('#goLoginBtn').attr('onclick', 'goBackLogin()');
});
