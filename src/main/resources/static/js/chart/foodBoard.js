/*
 * @(#)foodBoard.js        1.1.0 2022/2/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 음식 게시판 조회 요청 로직을 담고 있는 js
 * @author 양영준
 * @version 1.1.0 2022년 2월 22일
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
        }
    },

    findFood: function () {
        const signType = $("#searchType").find(":selected").val();
        let bloodSugar = $("#inputBloodSugar").val();
        if (bloodSugar === "") {
            bloodSugar = "0";
        }

        const foodPageForm = $("#foodPageForm");
        foodPageForm.find("[name='page']").val("1");
        foodPageForm.find("[name='sign']").val(signType);
        foodPageForm.find("[name='bloodSugar']").val(bloodSugar);

        let startDate = $("#modalStartDate").val();
        let endDate = $("#modalEndDate").val();

        const convertedDateForFoodBoard = DateConverter.convertStringToLocalDateTimeForFoodBoard(startDate, endDate);

        if (convertedDateForFoodBoard.converted === true) {// 잘 변환이 되었다면, submit()

            foodPageForm.find("[name='startYear']").val(convertedDateForFoodBoard.startYear);
            foodPageForm.find("[name='startMonth']").val(convertedDateForFoodBoard.startMonth);
            foodPageForm.find("[name='startDay']").val(convertedDateForFoodBoard.startDay);

            foodPageForm.find("[name='endYear']").val(convertedDateForFoodBoard.endYear);
            foodPageForm.find("[name='endMonth']").val(convertedDateForFoodBoard.endMonth);
            foodPageForm.find("[name='endDay']").val(convertedDateForFoodBoard.endDay);
            foodPageForm.submit();
        } else { // 변환이 이상하다면, alert()
            swal('', convertedDateForFoodBoard.errorMessage, "error");
        }
    }
};

const viewManipulator = {

    init: function () {
        const _this = this;

        _this.resolveATagValue();

        _this.resolvePreviousAndNext();

        _this.resolveFormAfterClickATag();

    },

    resolveATagValue: function () {

        const pageNumberLis = $("[id='pageNumberLi']");

        for (let pageNumberLi of pageNumberLis) {
            let pageNumberHref = pageNumberLi.querySelector('a');
            const pageId = Number(pageNumberHref.id) + 1;

            pageNumberHref.href = (pageId).toString();
            pageNumberHref.innerHTML = (pageId).toString();

            //만약 a 태그의 id와 히든 폼 페이지 값이 같다면, class를 active로 변경하여 다르게 표시한다.
            if (pageId === Number($("#foodPageForm").find("[name='page']").val())) {

                pageNumberLi.className = "page-item active";
            }

        }

    },
    resolvePreviousAndNext: function () {
        const previousPageATag = $("#previousPageLi").children('a');
        previousPageATag.attr("href", Number(previousPageATag.attr("id")) + 1);

        const nextPageATag = $("#nextPageLi").children('a');
        nextPageATag.attr("href", Number(nextPageATag.attr("id")) + 1);
    },

    resolveFormAfterClickATag: function () {
        const foodPageForm = $("#foodPageForm");

        $(".pagination a").click(function (e) {
            e.preventDefault();
            foodPageForm.find("[name='page']").val($(this).attr("href"));
            foodPageForm.submit();
        });
    }
};

$(document).ready(function () {
    viewManipulator.init();
    modalManipulator.init();
});