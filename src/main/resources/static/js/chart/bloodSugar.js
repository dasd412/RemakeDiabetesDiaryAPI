/*
 * @(#)bloodSugar.js        1.1.0 2022/2/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 식사 혈당 조회 요청 로직을 담고 있는 js
 * @author 양영준
 * @version 1.1.0 2022년 2월 22일
 */

/**
 * chart.js 설정 객체
 * @type {{data: {datasets: [{data: *[], label: string}]}, options: {scales: {xAxis: {time: {displayFormats: {day: string}, unit: string, unitStepSize: number}, type: string}}}, type: string}}
 */
const config = {
    type: 'scatter',
    data: {
        datasets: [
            {
                label: '아침 혈당',
                data: [],
                backgroundColor: 'rgb(72, 193, 72)',
                showLine: true,
                borderColor: 'rgb(72, 193, 72)'
            },
            {
                label: '점심 혈당',
                data: [],
                backgroundColor: 'rgb(72, 72, 193)',
                showLine: true,
                borderColor: 'rgb(72, 72, 193)'
            },
            {
                label: '저녁 혈당',
                data: [],
                backgroundColor: 'rgb(255, 99, 132)',
                showLine: true,
                borderColor: 'rgb(255, 99, 132)'
            }],
    },
    options: {
        scales: {
            xAxis: {

                type: 'time',
                time: {
                    unit: 'day',
                    unitStepSize: 1,
                    displayFormats: {
                        'day': 'MMM DD'
                    }
                }
            }
        }
    }
};

const myChartBloodSugar = new Chart(
    document.getElementById('myChartBloodSugar'),
    config
);

const bloodSugarFinder = {

    /**
     * 초기화 함수
     */
    init: function () {

        const _this = this;

        $("#findBloodSugarBtn").on('click', function () {
            _this.find();
        });
    },
    /**
     * 이벤트 : 조회버튼 클릭 시
     * 로직 : 차트 초기화 -> 체크 박스에 맞는 곳으로 진입
     */
    find: function () {
        resetChart();

        const checked = $("#blood-sugar-date-time-checkbox").is(":checked");

        if (checked === true) {
            this.findAllBloodSugar();
        } else {
            this.findBloodSugarBetweenDate();
        }
    },

    /**
     * 이벤트 : 조회 버튼 클릭시 + 전체 기간 체크 시
     * 전체 기간 식사 혈당 조회 요청
     */
    findAllBloodSugar: function () {
        $.ajax({
            type: 'GET',
            url: "/chart-menu/blood-sugar/all",
            contentType: 'application/json; charset=utf-8'
        }).done(function (apiResult) {
            updateChartForBloodSugar(apiResult);
        });
    },

    /**
     * 이벤트 : 조회 버튼 클릭시 + 일정 기간 체크 시
     * 해당 기간 식사 혈당 조회 요청
     */
    findBloodSugarBetweenDate: function () {
        const startDateForBloodSugar = $("#start-date-blood-sugar").val().split('/');
        const endDateForBloodSugar = $("#end-date-blood-sugar").val().split('/');

        const convertedDate = DateConverter.convertStringToLocalDateTime(startDateForBloodSugar, endDateForBloodSugar);

        if (convertedDate.isConverted === false) {
            swal('', convertedDate.errorMessage, "error");
            return;
        }

        $.ajax({
            type: 'GET',
            url: '/chart-menu/blood-sugar/between',
            dataType: 'json',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8;',
            data: convertedDate.betweenDate
        }).done(function (apiResult) {
            updateChartForBloodSugar(apiResult);
        });
    }
};

/**
 * 차트 데이터 초기화 함수
 */
function resetChart() {
    myChartBloodSugar.data.datasets.forEach((dataset) => {
        dataset.data = [];
    });
}

/**
 * GET mapping 리턴 이후의 로직이 중복되기 때문에 공통화시킨 함수.
 *
 * @param apiResult ajax 리턴 값
 */
function updateChartForBloodSugar(apiResult) {
    if (apiResult.success === false) {
        return;
    }

    for (let i = 0; i < apiResult.response.length; i++) {
        const dateTime = apiResult.response[i].dateTime;
        const bloodSugar = apiResult.response[i].bloodSugar;
        const eatTime = apiResult.response[i].eatTime;

        switch (eatTime) {
            case "BreakFast":
                myChartBloodSugar.data.datasets.forEach((dataset) => {
                    if (dataset.label === '아침 혈당') {
                        dataset.data.push({x: moment(dateTime), y: bloodSugar});
                    }
                });
                break;
            case "Lunch":
                myChartBloodSugar.data.datasets.forEach((dataset) => {
                    if (dataset.label === '점심 혈당') {
                        dataset.data.push({x: moment(dateTime), y: bloodSugar});
                    }
                });
                break;
            case "Dinner":
                myChartBloodSugar.data.datasets.forEach((dataset) => {
                    if (dataset.label === '저녁 혈당') {
                        dataset.data.push({x: moment(dateTime), y: bloodSugar});
                    }
                });
                break;
        }
    }
    myChartBloodSugar.update();
}

$(document).ready(function () {
    /**
     * 체크 박스 체크하면 시작 날짜 끝 날짜 안보임.
     */
    $("#blood-sugar-date-time-checkbox").change(function () {
        if ($("#blood-sugar-date-time-checkbox").is(":checked")) {
            $("#mini-calendar-blood-sugar").css("visibility", "hidden");
        } else {
            $("#mini-calendar-blood-sugar").css("visibility", "visible");
        }
    });

    bloodSugarFinder.init();
});