/*
 * @(#)profile.js        1.1.1 2022/2/28
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 일지 작성 폼을 담당
 *
 * @author 양영준
 * @version 1.1.1 2022년 2월 28일
 */

const ProfileManipulator = {
    isModalOn: false,

    init: function () {
        const _this = this;
        $("#updateProfileBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });

        $("#withdrawalBtn").on('click', function () {

        });

        $("#profileModalDecideBtn").on('click', function () {

        });

        $("#profileModalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#profileModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#profileModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    }
};

$(document).ready(function () {
    ProfileManipulator.init();
});