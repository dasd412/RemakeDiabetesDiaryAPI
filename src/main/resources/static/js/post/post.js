function addBreakFastFood() {
    console.log('addBreakFastFood');
    console.log($("#breakFastFoods li").length);

    const breakFastFoodName=$("#breakFastFoodName").val();

    if (breakFastFoodName.length<=0 || breakFastFoodName.length>50){
        swal('', "아침 음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }

    $("#breakFastFoods").append("<li>" +
        breakFastFoodName +
        "<span class='close'>x</span></li>"
        + "</li>");
}

function addLunchFood() {
    console.log('addLunchFood');
    console.log($("#lunchFoods li").length);

    const lunchFoodName=$("#lunchFoodName").val();

    if (lunchFoodName.length<=0 || lunchFoodName.length>50){
        swal('', "점심 음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }
    $("#lunchFoods").append("<li>" +
        lunchFoodName
        + "<span class='close'>x</span></li>"
        + "</li>");
}

function addDinnerFood() {
    console.log('addDinnerFood');
    console.log($("#dinnerFoods li").length);

    const dinnerFoodName=$("#dinnerFoodName").val();

    if (dinnerFoodName.length<=0 || dinnerFoodName.length>50){
        swal('', "저녁 음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }

    $("#dinnerFoods").append("<li>" +
        dinnerFoodName
        + "<span class='close'>x</span></li>"
        + "</li>");
}


function postDiary() {
    console.log("post");
}

function goToBack() {
    window.location.href = "/";
}

$(document).ready(function () {
    // let closeButtons = document.getElementsByClassName("close");
    // let i;
    //
    // for (i = 0; i < closeButtons.length; i++) {
    //     closeButtons[i].addEventListener("click", function () {
    //         this.parentElement.style.display = 'none';
    //     });
    // }

    $("#breakFastFoodsAddBtn").attr('onclick', 'addBreakFastFood()');
    $("#lunchFoodsAddBtn").attr('onclick', 'addLunchFood()');
    $("#dinnerFoodsAddBtn").attr('onclick', 'addDinnerFood()');
    $("#createBtn").attr('onclick', 'postDiary()');
    $("#cancelBtn").attr('onclick', 'goToBack()');
});
