const listSet = new Set();//자바의 Set과 동일함.

let locationOfMonth = 0;
let locationOfYear = 0;

const formatter = new Formatter();

function monthDayIndex(month, day) {
    for (let i = 0; i < month.length; i++) {
        if (month[i] === day) {
            return i;
        }
    }
}

function moveMonthPre() {
    locationOfMonth--;
    screenWriteMonth();
}

function moveMonthNext() {
    locationOfMonth++;
    screenWriteMonth();
}

function moveFastMonthPre() {
    locationOfYear--;
    screenWriteMonth();
}

function moveFastMonthNext() {
    locationOfYear++;
    screenWriteMonth();
}

function reInitTable() {
    $("#row1").children('td').html('');
    $("#row2").children('td').html('');
    $("#row3").children('td').html('');
    $("#row4").children('td').html('');
    $("#row5").children('td').html('');
    $("#row6").children('td').html('');
}


function screenWriteMonth() {
    let date = new Date();
    let month = date.getMonth() + 1 + locationOfMonth;

    if (month === 0) {
        locationOfYear--;
        locationOfMonth = 12 - Math.abs(locationOfMonth);
        month = date.getMonth() + 1 + locationOfMonth;
    } else if (month === 13) {
        locationOfYear++;
        locationOfMonth = locationOfMonth - 12;
        month = date.getMonth() + 1 + locationOfMonth;
    }

    let months = [month - 1, month, month + 1];

    if (month === 1) {
        months = [12, month, month + 1];
    } else if (month === 12) {
        months = [month - 1, month, 1];
    }

    let year = date.getFullYear() + locationOfYear;
    let monthDay = calendar.convertCalendarToArray(year, months[1]);

    let startIndex = monthDayIndex(monthDay, 1);
    let lastIndex = monthDayIndex(calendar.makeOneCalendarArray(year, months[1]), calendar.getMaxDateNumberOfYear(year, months[1])) + startIndex;

    let startDay = 0;
    let endDay = 0;

    reInitTable();

    for (let i = 0; i < monthDay.length; i++) {
        if (startDay === 1 && monthDay[i] === 1) {
            endDay = monthDay[i - 1];
        }
        if (startDay === 0 && monthDay[i] === 1) {
            startDay = 1;
        }

        let trIndex = parseInt(i / 7);
        let tr = $("#tbody tr").eq(trIndex);
        let td = tr.children().eq((i % 7) + 1);

        let monthForSchedule;

        let sb = new StringBuffer();

        if (i < startIndex) {
            if (months[0] === 12) {
                sb.append((year - 1));
                sb.append(months[0]);
                monthForSchedule = months[0];
                sb.append(monthDay[i]);
            } else {
                sb.append(year);
                sb.append(months[0]);
                monthForSchedule = months[0];
                sb.append(monthDay[i]);
            }
        } else if (i <= lastIndex) {
            sb.append(year);
            sb.append(months[1]);
            monthForSchedule = months[1];
            sb.append(monthDay[i]);
        } else {
            if (months[2] === 1) {
                sb.append((year + 1));
                sb.append(months[2]);
                monthForSchedule = months[2];
                sb.append(monthDay[i]);
            } else {
                sb.append(year);
                sb.append(months[2]);
                monthForSchedule = months[2];
                sb.append(monthDay[i]);

            }
        }


        td.attr("id", sb.toString());

        if(monthForSchedule===months[1]){
            td.html("<a onclick='scheduleAdd($(this).parent()," + year + "," + monthForSchedule + "," + monthDay[i] + ")'>" + formatter.formatNumber((monthDay[i]) + "</a>"));

            let a = td.children("a");

            a.on('mouseover', function () {
                td.css('cursor', 'pointer');
            });

            a.mouseleave(function () {
                td.css('cursor', 'default');
            });
        }
    }//날짜 그리기

    if (endDay === 0) {
        endDay = monthDay[monthDay.length - 1];
    }
    $("#yearMonth").text(year + "." + formatter.formatNumber(months[1]));

    findDiariesBetweenTime(year, months[1], startDay, endDay);
}//screen write month()

function scheduleAdd(td, year, month, day) {
    const diaryId = td.children('input').val();
    //일지 id가 없으면, post 폼으로 이동
    if (diaryId === undefined) {
        window.location.href = "/post?year=" + year + "&month=" + month + "&day=" + day;
    } else { //일지 id가 있으면 update - delete form 으로 이동.
        window.location.href = "/update-delete/" + diaryId;
    }
}

function findDiariesBetweenTime(year, month, startDay, endDay) {
    $.ajax({
        type: 'GET',
        url: '/api/diary/user/diabetes-diary/list?year=' + year + "&month=" + month + "&startDay=1" + "&endDay=" + endDay,
        contentType: 'application/json; charset=utf-8'
    }).done(function (e) {
        for (let i = 0; i < e.response.length; i++) {
            const diaryId = e.response[i].diaryId;
            const tdId = '#' + String(e.response[i].year) + String(e.response[i].month) + String(e.response[i].day);
            $(tdId).append("<input type='hidden' value='" + diaryId + "'/><span id=\"check\" class=\"fas fa-check\"></span>");
        }
    });
}

function calendarEventList() {
    screenWriteMonth();
}//get calendar list

$(document).ready(function () {
    calendarEventList();
    $('#pre').attr('onclick', 'moveMonthPre()');
    $('#next').attr('onclick', 'moveMonthNext()');
    $('#fastPre').attr('onclick', 'moveFastMonthPre()');
    $('#fastNext').attr('onclick', 'moveFastMonthNext()');
});