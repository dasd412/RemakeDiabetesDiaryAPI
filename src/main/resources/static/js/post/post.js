function FoodData(liId, name, amount) {
    this.liId = liId;
    this.name = name;
    this.amount = amount;
}

const PostManipulator = {
    foodNameSetDict: null,
    foodDataDict: null,
    foodIdDict: null,
    init: function () {
        const _this = this;

        //음식 이름 set (중복 방지)
        this.foodNameSetDict = {"breakFast": new Set(), "lunch": new Set(), "dinner": new Set()};

        //(liId, 음식 이름, 음식 양)의 딕셔너리
        this.foodDataDict = {"breakFast": [], "lunch": [], "dinner": []};

        //각 식사의 li id 지급용 딕셔너리 increment by 1
        this.foodIdDict = {"breakFast": 0, "lunch": 0, "dinner": 0};

        $("#breakFastFoodsAddBtn").attr('onclick', 'addFood(this)');
        $("#lunchFoodsAddBtn").attr('onclick', 'addFood(this)');
        $("#dinnerFoodsAddBtn").attr('onclick', 'addFood(this)');

        $("#createBtn").on('click', function () {
            _this.save();
        });
        $("#cancelBtn").on('click', function () {
            _this.goToBack();
        });
    },
    cacheAddFoods: function (liId, foodName, foodAmount, key) {
        //중복 여부를 효율적으로 판단할 수 있도록 set 에 음식 이름을 저장. alreadyHasFoodName()이 선행 호출되어 있어야 한다.
        this.foodNameSetDict[key].add(foodName);

        //딕셔너리[식단]에 (li 태그 id , 음식 이름, 음식 수량)의 데이터를 담아 놓는다.
        this.foodDataDict[key].push(new FoodData(liId, foodName, foodAmount));

        //auto increment 방식으로 id를 관리할 예정. 무조건 + 만 한다.
        this.foodIdDict[key] += 1
    },
    cacheRemoveFoods: function (liId, mealKey) {
        //mealKey 에 해당하는 dict 의 데이터 중에서 파라미터 liId 와 같은 li 태그 id를 가진 것을 찾는다.
        let i = 0;
        for (; i < this.foodDataDict[mealKey].length; i++) {
            if (this.foodDataDict[mealKey][i]['liId'] === liId) {
                break;
            }
        }
        //중복 여부를 검사하는 용도의 set 에서 해당 li 태그의 foodName 을 지운다.
        const foodName = this.foodDataDict[mealKey][i]['name'];
        this.foodNameSetDict[mealKey].delete(foodName);

        //캐시에서 해당 (li 태그 id , 음식 이름, 음식 수량) 데이터를 지운다.
        this.foodDataDict[mealKey].splice(i, 1);
    },
    alreadyHasFoodName: function (key, foodName) {
        //foodNameSetDict[식단] 에서 foodName 이 존재하는 지 체크하는 함수.
        switch (key) {
            case "breakFast":
                if (this.foodNameSetDict[key].has(foodName)) {
                    swal('', "아침 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "lunch":
                if (this.foodNameSetDict[key].has(foodName)) {
                    swal('', "점심 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "dinner":
                if (this.foodNameSetDict[key].has(foodName)) {
                    swal('', "저녁 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
        }

        return false;
    },
    getNextLiId: function (mealKey) {
        //mealKey 에 해당하는 식단의 다음 li 태그 id를 지급하는 함수.
        return this.foodIdDict[mealKey];
    },
    save: function () {
        //ajax 보내기전 유효성 검증
        const fastingPlasmaGlucose = $("#fastingPlasmaGlucose").val()
        const breakFastSugar = $("#breakFast").val();
        const lunchSugar = $("#lunch").val();
        const dinnerSugar = $("#dinner").val();

        if (fastingPlasmaGlucose !== "" && isNaN(fastingPlasmaGlucose)) {
            swal('', "공복혈당은 공백 또는 숫자로 표현해야 해요", "error");
            return;
        }

        if (breakFastSugar !== "" && isNaN(breakFastSugar)) {
            swal('', "아침혈당은 공백 또는 숫자로 표현해야 해요", "error");
            return;
        }

        if (lunchSugar !== "" && isNaN(lunchSugar)) {
            swal('', "점심혈당은 공백 또는 숫자로 표현해야 해요", "error");
            return;
        }

        if (dinnerSugar !== "" && isNaN(dinnerSugar)) {
            swal('', "저녁혈당은 공백 또는 숫자로 표현해야 해요", "error");
            return;
        }

        const data = {
            fastingPlasmaGlucose: fastingPlasmaGlucose,
            remark: $("#remark").val(),
            //todo 작성 날짜 데이터 추가 필요
            year: "2021",
            month: "12",
            day: "31",
            hour: "00",
            minute: "00",
            second: "00",
            breakFastSugar: breakFastSugar,
            lunchSugar: lunchSugar,
            dinnerSugar: dinnerSugar,
            breakFastFoods: this.foodDataDict['breakFast'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount']
            })),
            lunchFoods: this.foodDataDict['lunch'].map(elem => ({foodName: elem['name'], amount: elem['amount']})),
            dinnerFoods: this.foodDataDict['dinner'].map(elem => ({foodName: elem['name'], amount: elem['amount']}))
        };
        console.log(JSON.stringify(data));
        $.ajax({
            type: 'POST',
            url: '/api/diary/user/diabetes-diary',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function (response) {
            console.log(response);
        });

    },
    goToBack: function () {
        window.location.href = "/";
    }
}

// li 태그의 자식 요소인 span 의 'x' 가 눌려질 때 호출된다.
function closeList(span) {
    // span 의 부모인 li 태그의 id (발급 id)와 class (식단)을 찾는다.
    const liId = span.parentElement.id;
    const key = span.parentElement.className;

    //'x' 가 눌린 li 태그에 해당하는 캐시 값을 삭제해달라고 요청한다.
    PostManipulator.cacheRemoveFoods(Number(liId), key);

    //span 의 부모인 li 태그 제거
    span.parentNode.remove();
}

// 음식을 추가하는 '+' 버튼이 눌릴 때 호출된다.
function addFood(button) {
    let foodName;
    let ulName;
    let foodAmount;
    let mealKey;

    //버튼의 id 값에 따라 어떤 식단인지를 특정한다.
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

    //적절하지 않은 입력 값 검증하기
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

    //중복된 음식 이름의 데이터가 없는지 체크한다.
    if (!PostManipulator.alreadyHasFoodName(mealKey, foodName)) {
        //해당 식단에서 중복된 이름의 음식이 아니라면, auto increment 방식과 유사하게 li 태그의 다음 id 를 발급받는다.
        const liId = PostManipulator.getNextLiId(mealKey);

        //ul 태그에 li 태그를 동적으로 부착한다.
        $(ulName).append("<li>" +
            foodName + " "
            + foodAmount + "g" +
            "<span class='close' onclick='closeList(this)'>x</span></li>"
            + "</li>");
        //마지막으로 추가한 li 태그, 즉 방금 생성한 li 태그를 가리킨다.
        const lastLi = $(ulName).children().last();
        //해당 li 태그의 id (발급받은 id. 식단이 다르면 id가 같을 수는 있다.)와 class (어디 식단인가를 나타낸다.)를 넣는다.
        $(lastLi).attr('class', mealKey);
        $(lastLi).attr('id', liId);

        //캐시에 넣는다.
        PostManipulator.cacheAddFoods(liId, foodName, foodAmount, mealKey);
    }
}

$(document).ready(function () {
    PostManipulator.init();
});
