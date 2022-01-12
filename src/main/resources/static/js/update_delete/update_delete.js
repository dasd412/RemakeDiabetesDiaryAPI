//기존 데이터 수정, 삭제 시 사용되는 생성자. 기존 데이터이기 때문에 PK가 중요하다.
function OriginFoodData(id, name, amount) {
    this.id = id;
    this.name = name;
    this.amount = amount;
}

const UpdateDeleteManipulator = {

    //document ready 했을 때 최초의 데이터들 저장해 놓은 캐시.
    originalCache: {
        //일지 원본 데이터
        diaryId: $("#diaryId").val(),
        fastingPlasmaGlucose: $("#fastingPlasmaGlucose").val(),
        remark: $("#remark").val(),

        //식단 원본 데이터
        breakFastId: $("#breakFastPK").val(),
        breakFastSugar: $("#breakFast").val(),

        lunchId: $("#lunchPK").val(),
        lunchSugar: $("#lunch").val(),

        dinnerId: $("#dinnerPK").val(),
        dinnerSugar: $("#dinner").val(),

        //음식 원본 데이터
        breakFastFoods: [],
        lunchFoods: [],
        dinnerFoods: []
    },

    //원본 캐시에 저장될 음식 데이터 만들기 (실제 수정 시에는 삭제 작업이 이루어진다.)
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

        let liId = 0;
        $(eachLiSelector).each(function () {

            $(this).attr('class', eatTime);
            $(this).attr('id', liId);

            const foodId = $(this).children(foodIdSelector).val();
            $(this).children(".foodElement").each(function () {
                const foodName = $(this).children(".child.foodName").text();
                const amount = $(this).children(".child.amount").text();

                targetFoods.push(new OriginFoodData(foodId, foodName, amount));
                PostManipulator.cacheAddFoods(liId, foodName, amount, eatTime);
                liId++;
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

    //변경이 감지되었는지 체크하는 함수들.
    isDiaryModified: function () {
        return !(this.originalCache.fastingPlasmaGlucose === $("#fastingPlasmaGlucose").val() && this.originalCache.remark === $("#remark").val());
    },
    isBreakFastModified: function () {
        return !(this.originalCache.breakFastSugar === $("#breakFast").val());
    },
    isLunchModified: function () {
        return !(this.originalCache.lunchSugar === $("#lunch").val());
    },

    isDinnerModified: function () {
        return !(this.originalCache.dinnerSugar === $("#dinner").val());
    },

    update: function () {

        const data = {
            diaryId: this.originalCache.diaryId,
            fastingPlasmaGlucose: $("#fastingPlasmaGlucose").val(),
            remark: $("#remark").val(),
            diaryDirty: this.isDiaryModified(),

            breakFastId: this.originalCache.breakFastId,
            breakFastSugar: $("#breakFast").val(),
            breakFastDirty: this.isBreakFastModified(),

            lunchId: this.originalCache.lunchId,
            lunchSugar: $("#lunch").val(),
            lunchDirty: this.isLunchModified(),

            dinnerId: this.originalCache.dinnerId,
            dinnerSugar: $("#dinner").val(),
            dinnerDirty: this.isDinnerModified(),

            oldBreakFastFoods: this.originalCache.breakFastFoods,
            oldLunchFoods: this.originalCache.lunchFoods,
            oldDinnerFoods: this.originalCache.dinnerFoods,

            newBreakFastFoods: PostManipulator.foodDataDict['breakFast'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount']
            })),
            newLunchFoods: PostManipulator.foodDataDict['lunch'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount']
            })),
            newDinnerFoods: PostManipulator.foodDataDict['dinner'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount']
            }))
        }

        $.ajax({
            type: 'PUT',
            url: '/api/diary/user/diabetes-diary',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            window.location.href = "/";
        });

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

$(document).ready(function () {
    UpdateDeleteManipulator.init();
});