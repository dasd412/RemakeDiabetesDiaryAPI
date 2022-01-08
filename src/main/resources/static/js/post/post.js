function FoodData(name, amount) {
    this.name = name;
    this.amount = amount;
}

const PostManipulator = {
    breakFastFoods: null,
    lunchFoods: null,
    dinnerFoods: null,
    foodDataDict: null,
    foodIdDict: null,
    init: function () {
        //음식 이름 set (중복 방지)
        this.breakFastFoods = new Set();
        this.lunchFoods = new Set();
        this.dinnerFoods = new Set();

        //(음식 이름, 중량)의 딕셔너리
        this.foodDataDict = {"breakFast": [], "lunch": [], "dinner": []};

        //각 식사의 li id 지급용 딕셔너리 increment by 1
        this.foodIdDict = {"breakFast": 0, "lunch": 0, "dinner": 0};

        $("#breakFastFoodsAddBtn").attr('onclick', 'addFood(this)');
        $("#lunchFoodsAddBtn").attr('onclick', 'addFood(this)');
        $("#dinnerFoodsAddBtn").attr('onclick', 'addFood(this)');

        $("#createBtn").on('click', function () {
            this.save();
        });
        $("#cancelBtn").on('click', function () {
            this.goToBack();
        });
    },
    cacheAddFoods: function (foodName, foodAmount, key) {
        switch (key) {
            case "breakFast":
                this.breakFastFoods.add(foodName);
                this.foodDataDict[key].push(new FoodData(foodName, foodAmount));
                break;
            case "lunch":
                this.lunchFoods.add(foodName);
                this.foodDataDict[key].push(new FoodData(foodName, foodAmount));
                break;

            case "dinner":
                this.dinnerFoods.add(foodName);
                this.foodDataDict[key].push(new FoodData(foodName, foodAmount));
                break;
        }

        //auto increment 방식으로 id를 관리할 예정. 무조건 + 만 한다.
        this.foodIdDict[key] += 1
    },
    cacheRemoveFoods: function (e, key) {

    },
    alreadyHasFoodName: function (key, foodName) {
        switch (key) {
            case "breakFast":
                if (this.breakFastFoods.has(foodName)) {
                    swal('', "아침 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "lunch":
                if (this.lunchFoods.has(foodName)) {
                    swal('', "점심 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "dinner":
                if (this.dinnerFoods.has(foodName)) {
                    swal('', "저녁 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
        }

        return false;
    },
    getLiId: function (key) {
        return this.foodIdDict[key];
    },
    save: function () {

    },
    update: function () {

    },
    delete: function () {

    },
    goToBack: function () {
        window.location.href = "/";
    }
}

function closeList(span) {
    //, key, foodName, foodAmount
    console.log(span.parentElement.id, span.parentElement.className);

    // PostManipulator.cacheRemoveFoods(e);
    // e.parentElement.style.display = 'none';
}

function addFood(button) {
    let foodName;
    let ulName;
    let foodAmount;
    let mealKey;

    switch (button.id) {
        case "breakFastFoodsAddBtn":
            foodName = $("#breakFastFoodName").val();
            ulName = "#breakFastFoods";
            foodAmount = $("#breakFastFoodAmount").val();
            mealKey = "breakFast";
            break;

        case "lunchFoodsAddBtn":
            foodName = $("#lunchFoodName").val();
            ulName = "#lunchFoods";
            foodAmount = $("#lunchFoodAmount").val();
            mealKey = "lunch";
            break;

        case "dinnerFoodsAddBtn":
            foodName = $("#dinnerFoodName").val();
            ulName = "#dinnerFoods";
            foodAmount = $("#dinnerFoodAmount").val();
            mealKey = "dinner";
            break;
    }
    if (!isNaN(foodName)) {
        swal('', "음식 이름은 문자로 표현해야 해요", "error");
        return;
    }

    if (isNaN(foodAmount)) {
        swal('', "음식 양은 숫자로 표현해야 해요", "error");
        return;
    }

    if (foodName.length <= 0 || foodName.length > 50) {
        swal('', "음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }

    if (foodAmount <= 0 || foodAmount > 1000) {
        swal('', "음식 양은 1g 이상 1kg 미만이여야 합니다.", "error");
        return;
    }

    //중복된 데이터가 없을 경우에만 캐시 추가.
    if (!PostManipulator.alreadyHasFoodName(mealKey, foodName)) {
        const liId = PostManipulator.getLiId(mealKey);

        $(ulName).append("<li>" +
            foodName + " "
            + foodAmount + "g" +
            "<span class='close' onclick='closeList(this)'>x</span></li>"
            + "</li>");
        const lastLi = $(ulName).children().last();
        $(lastLi).attr('class', mealKey);
        $(lastLi).attr('id', liId);

        PostManipulator.cacheAddFoods(foodName, foodAmount, mealKey);
    }
}

$(document).ready(function () {
    PostManipulator.init();
});
