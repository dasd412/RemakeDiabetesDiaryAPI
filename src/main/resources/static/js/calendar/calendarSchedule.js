const listSet = new Set();//자바의 Set과 동일함.

let locationOfMonth = 0;
let locationOfYear = 0;


class HashMap {
    constructor() {
        this.map = [];
    }

    put(key, value) {
        this.map[key] = value;
    }

    get(key) {
        return this.map[key];
    }

    containsKey(key) {
        return key in this.map;
    }

    getAll() {
        return this.map;
    }

    clear() {
        this.map = [];
    }

    remove(key) {
        delete this.map[key];
    }

    keys() {
        const keys = [];
        for (let i in this.map) {
            keys.push(i);
        }
        return keys;
    }

    values() {
        const values = [];
        for (let i in this.map) {
            values.push(this.map[i]);
        }
        return values;
    }

    size() {
        let count = 0;
        for (let i in this.map) {
            count++;
        }
        return count;
    }

}//HashMap class

const hashMap = new HashMap();//(key,value)==(event의 id값(db id값이 아닌 날짜 기준 ), event 객체)

const formatter = new Formatter();

function scheduleAdd(year, month, day) {

    //todo 작성한 적 없을 때만 이동하도록 로직짜야 함.
    window.location.href = "/post?year=" + year + "&month=" + month + "&day=" + day;
}

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

let startDay = 0;
let endDay = 0;

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

    for (let i = 0; i < monthDay.length; i++) {
        if (startDay===1 && monthDay[i]===1){
            endDay=monthDay[i-1];
        }
        if(startDay===0 && monthDay[i]===1){
            startDay=1;
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


        td.html("<a onclick='scheduleAdd(" + year + "," + monthForSchedule + "," + monthDay[i] + ")'>" + formatter.formatNumber((monthDay[i]) + "</a>"));

        let a = td.children("a");

        a.on('mouseover', function () {
            td.css('cursor', 'pointer');
        });

        a.mouseleave(function () {
            td.css('cursor', 'default');
        });
    }//날짜 그리기

    for (let i = 0; i < 5; i++) {
        const tr = $("#tbody tr").eq(i);
        for (let j = 1; j <= 7; j++) {
            const td = tr.children().eq(j);
            if (hashMap.containsKey((td.attr("id")))) {
                const e = hashMap.get(td.attr("id"));
                td.append(eventTagFormat(e.fastingPlasmaGlucose));
            }
        }
    }
    if (endDay===0){
        endDay=monthDay[monthDay.length-1];
    }
    $("#yearMonth").text(year + "." + formatter.formatNumber(months[1]));

    findDiariesBetweenTime(year, months[1], startDay, endDay);
}//screen write month()

function findDiariesBetweenTime(year, month, startDay, endDay) {
    $.ajax({
        type: 'GET',
        url: '/api/diary/user/diabetes-diary/list?year=' + year + "&month=" + month + "&startDay=1" + "&endDay=" + endDay,
        contentType: 'application/json; charset=utf-8'
    }).done(function (e) {
        console.log(e.response);

    });
}

function eventTagFormat(fastingPlasmaGlucose) {

    const tag = new StringBuffer();

    tag.append("<p>");

    tag.append("blood sugar :" + fastingPlasmaGlucose);

    tag.append("</p>");

    return tag.toString();

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