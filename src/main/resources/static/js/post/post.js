/*
 * @(#)post.js        1.0.5 2022/2/5
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 일지 작성 폼을 담당
 *
 * @author 양영준
 * @version 1.0.5 2022년 2월 5일
 */

/**
 *
 * @param liId li 태그 id
 * @param name 음식 이름
 * @param amount 음식 수량
 * @param amountUnit 수량 단위
 * @constructor
 */
function FoodData(liId, name, amount, amountUnit) {
    this.liId = liId;
    this.name = name;
    this.amount = amount;
    this.amountUnit = amountUnit;
}


/**
 * 일지 작성 폼을 담당하는 객체
 *
 * @type {{init: PostManipulator.init, foodDataDict: null, alreadyHasFoodName: ((function(*, *): (boolean))|*), goToBack: PostManipulator.goToBack, foodNameSetDict: null, cacheAddFoods: PostManipulator.cacheAddFoods, cacheRemoveFoods: PostManipulator.cacheRemoveFoods, save: PostManipulator.save, foodIdDict: null, getNextLiId: (function(*): *), getDictLength: (function(*): *)}}
 */
const PostManipulator = {
    /**
     * 음식 이름 set
     */
    foodNameSetDict: null,

    /**
     * (liId, 음식 이름, 음식 양)의 딕셔너리
     */
    foodDataDict: null,

    /**
     * 각 식사의 li id 지급용 딕셔너리. 무조건 increment by 1 으로 id가 지급된다.
     */
    foodIdDict: null,

    /**
     * 일지 작성 폼 로딩시 호출되는 함수
     */
    init: function () {
        const _this = this;

        //음식 이름 set (중복 방지)
        this.foodNameSetDict = {"breakFast": new Set(), "lunch": new Set(), "dinner": new Set()};

        //(liId, 음식 이름, 음식 양)의 딕셔너리
        this.foodDataDict = {"breakFast": [], "lunch": [], "dinner": []};

        //각 식사의 li id 지급용 딕셔너리.
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

    /**
     * 이벤트 : 음식 추가용 버튼 ('+') 클릭 시
     * 로직 : 음식 이름 set에서 중복 체크 -> 음식 딕셔너리에 담아 놓기 -> 다음 li id 증가
     *
     * @param liId 현재 li 태그 id
     * @param foodName 음식 이름
     * @param foodAmount 음식 수량
     * @param foodAmountUnit 음식 수량 단위
     * @param mealKey 식사 시간. foodDataDict의 key.
     */
    cacheAddFoods: function (liId, foodName, foodAmount, foodAmountUnit, mealKey) {
        //중복 여부를 효율적으로 판단할 수 있도록 set 에 음식 이름을 저장. alreadyHasFoodName()이 선행 호출되어 있어야 한다.
        this.foodNameSetDict[mealKey].add(foodName);

        //딕셔너리[식단]에 (li 태그 id , 음식 이름, 음식 수량)의 데이터를 담아 놓는다.
        this.foodDataDict[mealKey].push(new FoodData(liId, foodName, foodAmount, foodAmountUnit));

        //auto increment 방식으로 id를 관리할 예정. 무조건 + 만 한다.
        this.foodIdDict[mealKey] += 1
    },

    /**
     * 이벤트 : 음식 li 태그에서 'x' 버튼 클릭시
     *
     * @param liId 'x' 버튼 클릭된 li 태그 id
     * @param mealKey 식사 시간. foodDataDict의 key.
     */
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
    /**
     *
     * @param mealKey 식사 시간. foodNameSetDict의 key.
     * @param foodName 음식 이름
     * @returns {boolean} foodNameSetDict에 이미 음식 이름이 존재하는 지 판별.
     */
    alreadyHasFoodName: function (mealKey, foodName) {
        //foodNameSetDict[식단] 에서 foodName 이 존재하는 지 체크하는 함수.
        switch (mealKey) {
            case "breakFast":
                if (this.foodNameSetDict[mealKey].has(foodName)) {
                    swal('', "아침 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "lunch":
                if (this.foodNameSetDict[mealKey].has(foodName)) {
                    swal('', "점심 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
            case "dinner":
                if (this.foodNameSetDict[mealKey].has(foodName)) {
                    swal('', "저녁 식사에 중복된 이름이 존재해요.", "error");
                    return true;
                }
                break;
        }

        return false;
    },
    /**
     *
     * @param mealKey 식사 시간. foodDataDict의 key.
     * @returns {number} 각 식단의 딕셔너리 길이 리턴. 길이 제한 8를 확인하기 위함.
     */
    getDictLength: function (mealKey) {
        return this.foodDataDict[mealKey].length;
    },
    /**
     *
     * @param mealKey 식사 시간. foodDataDict의 key.
     * @returns {number} mealKey 에 해당하는 식단의 다음 li 태그 id
     */
    getNextLiId: function (mealKey) {
        return this.foodIdDict[mealKey];
    },
    /**
     * 이벤트 : 작성하기 버튼 클릭
     * 로직 : 유효성 검증 -> ajax로 post 요청
     */
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

        const selectedMonth = $("#month").text();
        let month = "" + selectedMonth;
        if (selectedMonth < 10 && month.indexOf('0') === -1 || month.length === 1) {
            month = "0" + selectedMonth;
        }

        const selectedDay = $("#day").text();
        let day = "" + selectedDay;
        if (selectedDay < 10 && day.indexOf('0') === -1 || day.length === 1) {
            day = "0" + selectedDay;
        }

        const data = {
            fastingPlasmaGlucose: fastingPlasmaGlucose,
            remark: $("#remark").val(),
            year: $("#year").text(),
            month: month,
            day: day,
            hour: "00",
            minute: "00",
            second: "00",
            breakFastSugar: breakFastSugar,
            lunchSugar: lunchSugar,
            dinnerSugar: dinnerSugar,
            breakFastFoods: this.foodDataDict['breakFast'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: elem['amountUnit']
            })),
            lunchFoods: this.foodDataDict['lunch'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: elem['amountUnit']
            })),
            dinnerFoods: this.foodDataDict['dinner'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: elem['amountUnit']
            }))
        };

        $.ajax({
            type: 'POST',
            url: '/api/diary/user/diabetes-diary',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            window.location.href = "/calendar";
        });

    },
    /**
     * 달력 화면으로 돌아가기
     */
    goToBack: function () {
        window.location.href = "/calendar";
    }
}


/**
 * 이벤트 : li 태그의 자식 요소인 span 의 'x' 가 눌려질 때 호출된다.
 * @param span 'x'가 눌린 span
 */
function closeList(span) {
    // span 의 부모인 li 태그의 id (발급 id)와 class (식단)을 찾는다.
    const liId = span.parentElement.id;
    const key = span.parentElement.className;

    //'x' 가 눌린 li 태그에 해당하는 캐시 값을 삭제해달라고 요청한다.
    PostManipulator.cacheRemoveFoods(Number(liId), key);

    //span 의 부모인 li 태그 제거
    span.parentNode.remove();
}

/**
 * 이벤트 : 음식을 추가하는 '+' 버튼이 눌릴 때 호출된다.
 * @param button
 */
function addFood(button) {
    let foodName;
    let ulName;
    let foodAmount;
    let mealKey;
    let foodNameId;
    let foodAmountId;
    let foodAmountUnit;

    //버튼의 id 값에 따라 어떤 식단인지를 특정한다.
    switch (button.id) {
        case "breakFastFoodsAddBtn":
            foodNameId = "#breakFastFoodName";
            foodAmountId = "#breakFastFoodAmount";
            ulName = "#breakFastFoods";
            mealKey = "breakFast";

            break;

        case "lunchFoodsAddBtn":
            foodNameId = "#lunchFoodName";
            foodAmountId = "#lunchFoodAmount";
            ulName = "#lunchFoods";
            mealKey = "lunch";
            break;

        case "dinnerFoodsAddBtn":
            foodNameId = "#dinnerFoodName";
            foodAmountId = "#dinnerFoodAmount";
            ulName = "#dinnerFoods";
            mealKey = "dinner";
            break;
    }

    foodName = $(foodNameId).val();
    foodAmount = $(foodAmountId).children('input').val();
    foodAmountUnit = $(foodAmountId).children('select').children("option:selected");

    //적절하지 않은 입력 값 검증하기
    if (!isNaN(foodName)) {
        swal('', "음식 이름은 문자로 표현해야 해요", "error");
        return;
    }

    if (foodAmount !== "" && isNaN(foodAmount)) {
        swal('', "음식 양은 공백이나 숫자로 표현해야 해요", "error");
        return;
    }

    if (foodName.length <= 0 || foodName.length > 50) {
        swal('', "음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }

    //각 식단에서 길이가 8이하 인지 확인.
    if (PostManipulator.getDictLength(mealKey) >= 8) {
        swal('', mealKey + "'의 음식 개수는 최대 8개 입니다.", "error");
        return;
    }
    //중복된 음식 이름의 데이터가 없는지 체크한다.
    if (!PostManipulator.alreadyHasFoodName(mealKey, foodName)) {

        //해당 식단에서 중복된 이름의 음식이 아니라면, auto increment 방식과 유사하게 li 태그의 다음 id 를 발급받는다.
        const liId = PostManipulator.getNextLiId(mealKey);

        if (foodAmount === "") {
            $(ulName).append("<li>" +
                foodName +
                "<span class=\"fas fa-times\" id='close' onclick='closeList(this)'></span></li>"
                + "</li>");

        } else {
            //ul 태그에 li 태그를 동적으로 부착한다.
            $(ulName).append("<li>" +
                foodName + " "
                + foodAmount + " " + foodAmountUnit.text() +
                "<span class=\"fas fa-times\" id='close' onclick='closeList(this)'></span></li>"
                + "</li>");

        }

        //마지막으로 추가한 li 태그, 즉 방금 생성한 li 태그를 가리킨다.
        const lastLi = $(ulName).children().last();

        //해당 li 태그의 id (발급받은 id. 식단이 다르면 id가 같을 수는 있다.)와 class (어디 식단인가를 나타낸다.)를 넣는다.
        $(lastLi).attr('class', mealKey);
        $(lastLi).attr('id', liId);

        //캐시에 넣는다.
        PostManipulator.cacheAddFoods(liId, foodName, foodAmount, foodAmountUnit.val(), mealKey);
    }

    $(foodNameId).val('');
    $(foodAmountId).children('input').val('');

}

$(document).ready(function () {
    PostManipulator.init();
});
