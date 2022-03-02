/*
 * @(#)findInfo.js        1.1.2 2022/3/2
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * id 찾기, 비밀 번호 찾기 담당
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 2일
 */

const FindIdManipulator = {

    isModalOn: false,

    init: function () {
        const _this = this;
        $("#callModalIdBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
        $("#cancelFindIdBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },

    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#findIdModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#findIdModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    }

};

const FindPasswordManipulator = {

    isModalOn: false,

    init: function () {
        const _this = this;
        $("#callModalPasswordBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });

        $("#cancelFindPasswordBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#findPasswordModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#findPasswordModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    }
};

$(document).ready(function () {
    FindIdManipulator.init();
    FindPasswordManipulator.init();
});
