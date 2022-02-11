/*
 * @(#)foodBoard.js        1.0.7 2022/2/11
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

const viewManipulator = {

    init: function () {
        const _this = this;

        _this.viewATagValue();

        const previousPageATag = $("#previousPageLi").children('a');
        const nextPageATag = $("#nextPageLi").children('a');


    },

    viewATagValue: function () {

        const pageNumberLis = $("[id='pageNumberLi']");

        for (let pageNumberLi of pageNumberLis) {
            let pageNumberHref = pageNumberLi.querySelector('a');
            let pageNumber = Number(pageNumberHref.id);
            pageNumberHref.innerHTML = pageNumber + 1;
        }

    }
};

$(document).ready(function () {
    viewManipulator.init();
    modalManipulator.init();
});