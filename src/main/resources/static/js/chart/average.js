/*
 * @(#)bloodSugar.js        1.0.4 2022/2/4
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

const averageFormatter = new Formatter();

const config = {
    type: 'bar',
    data: {
        labels: ["평균 공복 혈당", "평균 아침 혈당", "평균 점심 혈당", "평균 저녁 혈당", "아침,점심,저녁 합산 평균"],
        datasets: [{
            label: "평균 혈당 차트",
            data: [],
            backgroundColor: [
                'rgba(255, 159, 64)',
                'rgba(223, 230, 22)',
                'rgba(72, 193, 72)',
                'rgba(72, 72, 193)',
                'rgba(255, 99, 132)',
                'rgba(212, 35, 232)'
            ],
            borderColor: [
                'rgb(255, 159, 64)',
                'rgb(223, 230, 22)',
                'rgb(72, 193, 72)',
                'rgb(72, 72, 193)',
                'rgb(255, 99, 132)',
                'rgb(212, 35, 232)'
            ],
        }]
    },
    options: {
        scales: {
            y: {
                beginAtZero: true
            }
        }

    }
};

const myChartAverage = new Chart(
    document.getElementById('myChartAverage'),
    config
);


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
            updateChart(apiResult);
        }).fail(function () {
            swal('', "데이터가 존재하지 않아요!", "error");
        });
    },
    /**
     * 이벤트 : 조회 버튼 클릭시 + 일정 기간 체크 시
     * 해당 기간 평균 혈당들 조회
     */
    findAverageBetween: function () {

        const startDateAverage = $("#start-date-average").val().split('/');
        const endDateAverage = $("#end-date-average").val().split('/');

        const startYearForAverage = startDateAverage[2];
        const startMonthForAverage = averageFormatter.formatString(startDateAverage[0]);
        const startDayForAverage = averageFormatter.formatString(startDateAverage[1]);

        const endYearForAverage = endDateAverage[2];
        const endMonthForAverage = averageFormatter.formatString(endDateAverage[0]);
        const endDayForAverage = averageFormatter.formatString(endDateAverage[1]);

        //Date 객체에서 월은 0부터 시작
        const startDateOfAverage = new Date(startYearForAverage, startMonthForAverage - 1, startDayForAverage);
        const endDateOfAverage = new Date(endYearForAverage, endMonthForAverage - 1, endDayForAverage);

        if (startMonthForAverage === undefined || startDayForAverage === undefined) {
            swal('', "시작 날짜를 입력해주세요", "error");
            return;
        }
        if (endMonthForAverage === undefined || endDayForAverage === undefined) {
            swal('', "끝 날짜를 입력해주세요", "error");
            return;
        }
        //끝 날짜가 시작 날짜보다 앞서면 안된다.
        if (startDateOfAverage > endDateOfAverage) {
            swal('', "끝 날짜가 시작 날짜보다 앞서면 안되요!", "error");
            return;
        }
        const betweenDate = {
            startYear: startYearForAverage,
            startMonth: startMonthForAverage,
            startDay: startDayForAverage,

            endYear: endYearForAverage,
            endMonth: endMonthForAverage,
            endDay: endDayForAverage
        };

        $.ajax({
            type: 'GET',
            url: '/chart-menu/average/between',
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8;',
            data: betweenDate
        }).done(function (apiResult) {
            updateChart(apiResult);
        }).fail(function () {
            swal('', "데이터가 존재하지 않아요!", "error");
        });
    }
};

/**
 * 차트 데이터 초기화 함수
 */
function resetChart() {
    myChartAverage.data.datasets.forEach((dataset) => {
        dataset.data = [];
    });
}

/**
 * 차트 데이터 갱신 함수
 * @param apiResult 서버로부터 얻은 response
 */
function updateChart(apiResult) {

    myChartAverage.data.datasets.forEach((dataset) => {
        dataset.data = [
            apiResult.response.averageFpg,
            apiResult.response.averageBreakFast,
            apiResult.response.averageLunch,
            apiResult.response.averageDinner,
            apiResult.response.averageBloodSugar,
        ];
    });

    myChartAverage.update();
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