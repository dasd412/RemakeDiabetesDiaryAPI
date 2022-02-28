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

        $("#profileModalDecideBtn").on('click', function () {
            _this.updateProfile();
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
    },
    updateProfile: function () {

        const data = {
            diabetesPhase: $("#phaseSelector option:selected").val()
        };

        $.ajax({
            type: 'PUT',
            url: '/profile/info',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            location.reload();
        });
    }

};

const WithDrawlManipulator = {

    isModalOn: false,

    init: function () {
        const _this = this;

        $("#withdrawalBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
        $("#withdrawalModalDecideBtn").on('click', function () {
            _this.withdraw();
        });
        $("#withdrawalModalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#withdrawalModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#withdrawalModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    },
    withdraw: function () {
        $.ajax({
            type: 'DELETE',
            url: "/profile/withdrawal",
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function (){
            window.location.href = "/";
        });
    }
};

$(document).ready(function () {
    ProfileManipulator.init();
    WithDrawlManipulator.init();
});