function apiPostTest() {

    const data = {
        writerId: 1,
        fastingPlasmaGlucose: 100,
        remark: "test",
        year: "2022",
        month: "01",
        day: "03",
        hour: "00",
        minute: "00",
        second: "00"
    };

    $.ajax({
        type: 'POST',
        url: 'api/diary/diabetes-diary',
        dataType: 'json',
        contentType: 'application/json;charset=utf-8',
        data: JSON.stringify(data)
    }).done(function () {
        console.log("success");
    }).fail(function () {
        console.log("fail");
    });
}

function apiGetTest() {

    $.ajax({
        type: 'GET',
        url: 'api/diary/user/diabetes-diary/list',
        contentType: 'application/json; charset=utf-8',
    }).done(function (data) {
        console.log(data);
    }).fail(function (data) {
        console.log("fail");
    });
}


$(document).ready(function () {
    $('#apiPostBtn').attr('onclick', 'apiPostTest()');
    $('#apiGetBtn').attr('onclick', 'apiGetTest()');
});
