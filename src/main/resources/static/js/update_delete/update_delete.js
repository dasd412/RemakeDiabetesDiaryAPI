const FoodData = {
    id: null,
    name: null,
    amount: null,

    //기존 데이터 수정, 삭제 시 사용되는 생성자. 기존 데이터이기 때문에 PK가 중요하다.
    initForOld: function (id, name, amount) {
        this.id = id;
        this.name = name;
        this.amount = amount;
    },

    //새로운 데이터 삽입 시 사용되는 생성자. 새로운 데이터는 db에 아직 없으므로 PK가 없다.
    initForNew: function (name, amount) {
        this.name = name;
        this.amount = amount;
    }
}

const UpdateDeleteManipulator = {

    //document ready 했을 때 최초의 데이터들
    originalCache: {
        //일지 원본 데이터
        fastingPlasmaGlucose: $("#fastingPlasmaGlucose").val(),
        remark: $("#remark").val(),

        //식단 원본 데이터
        breakFastSugar: $("#breakFast").val(),
        lunchSugar: $("#lunch").val(),
        dinnerSugar: $("#dinner").val()

        //음식 원본 데이터

    },

    init: function () {
        const _this = this;

        console.log("origin");
        console.log(this.originalCache);

        $("#updateBtn").on('click', function () {
            _this.update();
        });

        $("#deleteBtn").on('click', function () {
            _this.delete();
        });

        $("#updateCancelBtn").on('click', function () {
            _this.goToBack();
        });
    },

    update: function () {
        const diaryId = $("#diaryId").val();

    },

    delete: function () {
        const diaryId = $("#diaryId").val();
        $.ajax({
            type: 'DELETE',
            url: '/api/diary/user/diabetes-diary/' + diaryId,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function () {
            window.location.href = "/";
        })
    },

    goToBack: function () {
        window.location.href = "/";
    }
}

function modifyFood(){

}


$(document).ready(function () {
    UpdateDeleteManipulator.init();
});