/*
 * @(#)update_delete.js        1.0.5 2022/2/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 일지 작성 폼을 담당
 *
 * @author 양영준
 * @version 1.0.5 2022년 2월 6일
 */

/**
 * 기존 데이터 수정, 삭제 시 사용되는 생성자. 기존 데이터이기 때문에 PK가 중요하다.
 *
 * @param id 기본키
 * @param name  음식 이름
 * @param amount 음식 수량
 * @param amountUnit 음식 수량 단위
 * @constructor
 */
function OriginFoodData(id, name, amount, amountUnit) {
    this.id = id;
    this.name = name;
    this.amount = amount;
    if (this.amountUnit === "" || this.amountUnit === null || this.amountUnit === undefined) {
        this.amountUnit = "NONE";
    } else if (this.amountUnit === "개") {
        this.amountUnit = "count";
    } else {
        this.amountUnit = amountUnit;
    }
}

/**
 * 일지 수정 및 삭제를 담당하는 객체
 * @type {{isLunchModified: (function(): boolean), init: UpdateDeleteManipulator.init, isBreakFastModified: (function(): boolean), goToBack: UpdateDeleteManipulator.goToBack, isDinnerModified: (function(): boolean), update: UpdateDeleteManipulator.update, makeOriginFoodData: UpdateDeleteManipulator.makeOriginFoodData, isDiaryModified: (function(): boolean), originalCache: {dinnerSugar: (jQuery|*|string), fastingPlasmaGlucose: (jQuery|*|string), lunchId: (jQuery|*|string), lunchSugar: (jQuery|*|string), breakFastSugar: (jQuery|*|string), diaryId: (jQuery|*|string), lunchFoods: *[], dinnerFoods: *[], remark: (jQuery|*|string), breakFastId: (jQuery|*|string), dinnerId: (jQuery|*|string), breakFastFoods: *[]}, delete: UpdateDeleteManipulator.delete}}
 */
const UpdateDeleteManipulator = {

    /**
     * document ready 했을 때 최초의 데이터들 저장해 놓은 캐시.
     */
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

    /**
     * 원본 캐시에 저장될 음식 데이터 만들기 (실제 수정 시에는 삭제 작업이 이루어진다.)
     * @param eatTime 식사 시간
     */
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
                const amountUnit = $(this).children(".child.gram").text();

                targetFoods.push(new OriginFoodData(foodId, foodName, amount, amountUnit));
                PostManipulator.cacheAddFoods(liId, foodName, amount, amountUnit, eatTime);
                liId++;
            });
        });

    },
    /**
     * 문서 첫 로딩시 호출된다.
     */
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

    /**
     *  변경이 감지되었는지 체크하는 함수들.
     */
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

    /**
     * 이벤트 : 수정하기 버튼 클릭 시
     * 로직 : 일지, 식단은 수정된 대로 반영. 그러나 음식은 기존 엔티티 전부 삭제 요청 및 새로 삽입 요청
     */
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

            /**
             * 삭제될 기존 음식 엔티티들
             */
            oldBreakFastFoods: this.originalCache.breakFastFoods,
            oldLunchFoods: this.originalCache.lunchFoods,
            oldDinnerFoods: this.originalCache.dinnerFoods,

            /**
             * 새로 삽입될 음식 엔티티들
             */
            newBreakFastFoods: PostManipulator.foodDataDict['breakFast'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: this.convertAmountUnit(elem)
            })),
            newLunchFoods: PostManipulator.foodDataDict['lunch'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: this.convertAmountUnit(elem)
            })),
            newDinnerFoods: PostManipulator.foodDataDict['dinner'].map(elem => ({
                foodName: elem['name'],
                amount: elem['amount'],
                amountUnit: this.convertAmountUnit(elem)
            }))
        };


        $.ajax({
            type: 'PUT',
            url: '/api/diary/user/diabetes-diary',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            window.location.href = "/calendar";
        });

    },
    convertAmountUnit:function (elem){
        if(elem['amountUnit']===""){
            return "NONE";
        }
        else if(elem['amountUnit']==='개'){
            return 'count';
        }
        else{
            return elem['amountUnit'];
        }
    },
    /**
     * 이벤트 : 삭제하기 버튼 클릭 시
     */
    delete: function () {
        const diaryId = $("#diaryId").val();
        $.ajax({
            type: 'DELETE',
            url: '/api/diary/user/diabetes-diary/' + diaryId,
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        });
        window.location.href = "/calendar";
    },

    /**
     * 달력 화면으로 돌아가기
     */
    goToBack: function () {
        window.location.href = "/calendar";
    }
}

$(document).ready(function () {
    UpdateDeleteManipulator.init();
});