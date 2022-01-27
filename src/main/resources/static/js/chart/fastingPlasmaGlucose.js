/*
 * @(#)fastingPlasmaGlucose.js        1.0.2 2022/1/26
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */


/**
 * chart.js 설정 객체
 * @type {{data: {datasets: {backgroundColor: string, data: [{x: number, y: number},{x: number, y: number},{x: number, y: number},{x: number, y: number}], label: string}[]}, options: {scales: {x: {position: string, type: string}}}, type: string}}
 */
const config = {
    type: 'scatter',
    data: {
        datasets: [{
            label: '공복 혈당',
            data: [],
            backgroundColor: 'rgb(255, 99, 132)'
        }],
    },
    options: {
        scales: {
            x: {
                ticks: {
                    display: false
                }
            }
        }
    }
};

const myChart = new Chart(
    document.getElementById('myChart'),
    config
);

const FpgFinder = {

    /**
     * 초기화 함수
     */
    init: function () {

        const _this = this;

        $("#findFpgBtn").on('click', function () {
            _this.find();
        });

    },

    /**
     * 이벤트 : 조회버튼 클릭 시
     */
    find: function () {
        const checked = $("#date-time-checkbox").is(":checked");
        if (checked === true) {
            this.findAll();
        } else {
            this.findBetweenDate();
        }
    },

    /**
     * 이벤트 : 조회 버튼 클릭시 + 전체 기간 체크 시
     * 전체 기간 공복 혈당 조회 요청
     */
    findAll: function () {
        $.ajax({
            type: 'GET',
            url: "/chart-menu/fasting-plasma-glucose/all",
            contentType: 'application/json; charset=utf-8'
        }).done(function (e) {
            if (e.success === false) {
                return;
            }
            //{x:timeStamp,y:fpg}
            for (let i = 0; i < e.response.length; i++) {
                const timeStamp = e.response[i].timeByTimeStamp;
                const fpg = e.response[i].fastingPlasmaGlucose;
                myChart.data.datasets.forEach((dataset) => {
                    dataset.data[i] = {x: timeStamp, y: fpg};
                });
            }
            myChart.update();
        });
    },

    /**
     * 이벤트 : 조회 버튼 클릭시 + 일정 기간 체크 시
     * 해당 기간 공복 혈당 조회 요청
     */
    findBetweenDate: function () {

    }
};

$(document).ready(function () {
    /**
     * 체크 박스 체크하면 시작 날짜 끝 날짜 안보임.
     */
    $("#date-time-checkbox").change(function(){
       if($("#date-time-checkbox").is(":checked")){
            $("#mini-calendar").css("visibility","hidden");
       }else{
           $("#mini-calendar").css("visibility","visible");
       }
    });

    FpgFinder.init();
});