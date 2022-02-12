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
        }
    },

    findFood: function () {
        const signType = $("#searchType").find(":selected").val();
        let bloodSugar = $("#inputBloodSugar").val();
        if (bloodSugar === "") {
            bloodSugar = "0";
        }
        let startDate = $("#modalStartDate").val();
        let endDate = $("#modalEndDate").val();

        const foodPageForm = $("#foodPageForm");
        foodPageForm.find("[name='page']").val("1");
        foodPageForm.find("[name='sign']").val(signType);
        foodPageForm.find("[name='bloodSugar']").val(bloodSugar);

        let startYear = "";
        let startMonth = "";
        let startDay = "";

        let endYear = "";
        let endMonth = "";
        let endDay = "";

        let convertedStartDate = false;
        if (startDate != null && startDate !== "") {
            startDate = startDate.split("/");
            startYear = startDate[2];
            startMonth = foodBoardFormatter.formatString(startDate[0]);
            startDay = foodBoardFormatter.formatString(startDate[1]);
            convertedStartDate = true;
        }

        let convertedEndDate = false;
        if (endDate != null && endDate !== "") {
            endDate = endDate.split("/");
            endYear = endDate[2];
            endMonth = foodBoardFormatter.formatString(endDate[0]);
            endDay = foodBoardFormatter.formatString(endDate[1]);
            convertedEndDate = true;
        }

        if (convertedStartDate === true && convertedEndDate === true) {
            //Date 객체에서 월은 0부터 시작
            const startDateOfBloodSugar = new Date(startYear, startMonth - 1, startDay);
            const endDateOfBloodSugar = new Date(endYear, endMonth - 1, endDay);
            if (startDateOfBloodSugar > endDateOfBloodSugar) {
                swal('', "끝 날짜가 시작 날짜보다 앞서면 안되요!", "error");
                return;
            }

            foodPageForm.find("[name='startYear']").val(startYear);
            foodPageForm.find("[name='startMonth']").val(startMonth);
            foodPageForm.find("[name='startDay']").val(startDay);

            foodPageForm.find("[name='endYear']").val(endYear);
            foodPageForm.find("[name='endMonth']").val(endMonth);
            foodPageForm.find("[name='endDay']").val(endDay);
        }

        foodPageForm.submit();
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