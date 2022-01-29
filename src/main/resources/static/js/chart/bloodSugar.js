/*
 * @(#)bloodSugar.js        1.0.3 2022/1/29
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

const bloodSugarFormatter = new Formatter();

const bloodSugarFinder = {

    init: function () {

    }

};

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