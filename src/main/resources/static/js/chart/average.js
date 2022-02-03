/*
 * @(#)bloodSugar.js        1.0.4 2022/2/3
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

const averageFormatter = new Formatter();

// const myChartAverage = new Chart(
//     document.getElementById('myChartAverage'),
//     config
// );


const averageFinder = {
    /**
     * 초기화 함수
     */
    init: function () {
        const _this = this;

        $("#findAverageBtn").on('click', function () {
            _this.find();
        });
    },

    /**
     * 이벤트 : 조회버튼 클릭 시
     * 로직 : 차트 초기화 -> 체크 박스에 맞는 곳으로 진입
     */
    find: function () {
        resetChart();

        const checked = $("#average-date-time-checkbox").is(":checked");

        if (checked === true) {
            this.findAllAverage();
        } else {
            this.findAverageBetween();
        }
    },
    /**
     * 이벤트 : 조회 버튼 클릭시 + 전체 기간 체크 시
     * 전체 기간 평균 혈당들 조회
     */
    findAllAverage: function () {
        $.ajax({
            type: 'GET',
            url: "/chart-menu/average/all",
            contentType: 'application/json; charset=utf-8'
        }).done(function (apiResult) {

        });
    },
    /**
     * 이벤트 : 조회 버튼 클릭시 + 일정 기간 체크 시
     * 해당 기간 평균 혈당들 조회
     */
    findAverageBetween: function () {

    }
};

/**
 * 차트 데이터 초기화 함수
 */
function resetChart() {

}

$(document).ready(function () {
    /**
     * 체크 박스 체크하면 시작 날짜 끝 날짜 안보임.
     */
    $("#average-date-time-checkbox").change(function () {
        if ($("#average-date-time-checkbox").is(":checked")) {
            $("#mini-calendar-average").css("visibility", "hidden");
        } else {
            $("#mini-calendar-average").css("visibility", "visible");
        }
    });

    averageFinder.init();
});