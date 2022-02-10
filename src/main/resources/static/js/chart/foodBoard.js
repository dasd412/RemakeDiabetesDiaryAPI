/*
 * @(#)foodBoard.js        1.0.6 2022/2/8
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

const foodBoardFormatter = new Formatter();

const modalManipulator = {

    isModalOn: false,

    init: function () {

        const _this = this;

        $("#searchBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });

        $("#modalDecideBtn").on('click', function () {
            _this.findFood();
        });

        $("#modalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },

    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#searchModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#searchModal").attr("style", "display:none;");
            this.isModalOn = false;
            this.resetModal();
        }
    },

    resetModal: function () {
        $("#inputBloodSugar").val('');
        $("#modalStartDate").val('');
        $("#modalEndDate").val('');
    },

    findFood: function () {
        // $.ajax({
        //     type: 'GET',
        //     url: '/chart-menu/food-board/list',
        //     dataType: 'json',
        //     contentType: 'application/x-www-form-urlencoded; charset=UTF-8;',
        // }).done(function () {
        //
        // });
    }
};

$(document).ready(function () {
    modalManipulator.init();
});