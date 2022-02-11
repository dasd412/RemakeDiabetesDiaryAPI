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

                pageNumberLi.className="page-item active";
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