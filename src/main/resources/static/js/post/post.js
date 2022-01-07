function addBreakFastFood() {
    console.log('addBreakFastFood');
    console.log($("#breakFastFoods li").length);

    const breakFastFoodName=$("#breakFastFoodName").val();
    $("#breakFastFoods").append("<li>" +
        breakFastFoodName +
        "<span class='close'>x</span></li>"
        + "</li>");
}

function addLunchFood() {
    console.log('addLunchFood');
    console.log($("#lunchFoods li").length);
    const lunchFoodName=$("#lunchFoodName").val();
    $("#lunchFoods").append("<li>" +
        lunchFoodName
        + "<span class='close'>x</span></li>"
        + "</li>");
}

function addDinnerFood() {
    console.log('addDinnerFood');
    console.log($("#dinnerFoods li").length);
    const dinnerFoodName=$("#dinnerFoodName").val();
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
