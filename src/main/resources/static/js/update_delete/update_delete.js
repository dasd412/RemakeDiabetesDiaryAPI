//기존 데이터 수정, 삭제 시 사용되는 생성자. 기존 데이터이기 때문에 PK가 중요하다.
function OriginFoodData(id, name, amount) {
    this.id = id;
    this.name = name;
    this.amount = amount;
};

//새로운 데이터 삽입 시 사용되는 생성자. 새로운 데이터는 db에 아직 없으므로 PK가 없다.
function FoodData(name, amount) {
    this.name = name;
    this.amount = amount;
};


const UpdateDeleteManipulator = {

    //document ready 했을 때 최초의 데이터들 저장해 놓은 캐시.
    originalCache: {
        // //일지 원본 데이터
        // diaryId: $("#diaryId").val(),
        // fastingPlasmaGlucose: $("#fastingPlasmaGlucose").val(),
        // remark: $("#remark").val(),
        //
        // //식단 원본 데이터
        // breakFastId: $("#breakFastPK").val(),
        // breakFastSugar: $("#breakFast").val(),
        //
        // lunchId: $("#lunchPK").val(),
        // lunchSugar: $("#lunch").val(),
        //
        // dinnerId: $("#dinnerPK").val(),
        // dinnerSugar: $("#dinner").val(),

        //음식 원본 데이터
        breakFastFoods: [],
        lunchFoods: [],
        dinnerFoods: []
    },

    makeOriginFoodData: function (eatTime) {
        let eachLiSelector;
        let foodIdSelector;
        let targetFoods;
        switch (eatTime) {
            case "breakFast":
                eachLiSelector = "#breakFastFoods li";
                foodIdSelector = ".breakFastFood";
                targetFoods = this.originalCache.breakFastFoods;
                break;
            case "lunch":
                eachLiSelector = "#lunchFoods li";
                foodIdSelector = ".lunchFood";
                targetFoods = this.originalCache.lunchFoods;
                break;
            case "dinner":
                eachLiSelector = "#dinnerFoods li";
                foodIdSelector = ".dinnerFood";
                targetFoods = this.originalCache.dinnerFoods;
                break;
        }

        $(eachLiSelector).each(function () {
            const foodId = $(this).children(foodIdSelector).val();
            $(this).children(".foodElement").each(function () {
                const foodName = $(this).children(".child.foodName").text();
                const amount = $(this).children(".child.amount").text();
                targetFoods.push(new OriginFoodData(foodId, foodName, amount));
            });
        });

    },
    init: function () {
        const _this = this;

        this.makeOriginFoodData("breakFast");
        this.makeOriginFoodData("lunch");
        this.makeOriginFoodData("dinner");

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

function modifyFood() {

}


$(document).ready(function () {
    UpdateDeleteManipulator.init();
});