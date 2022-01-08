function closeList(e){
    e.parentElement.style.display='none';
}

function addFood(button){
    let foodName;
    let ulName;


    if(button.id==="breakFastFoodsAddBtn"){
        foodName=$("#breakFastFoodName").val();
        ulName="#breakFastFoods";
        let breakFastLength=$("#breakFastFoods li").length;
    }else if (button.id==="lunchFoodsAddBtn"){
        foodName=$("#lunchFoodName").val();
        ulName="#lunchFoods";
        let lunchLength=$("#lunchFoods li").length;
    }else{
        foodName=$("#dinnerFoodName").val();
        ulName="#dinnerFoods";
        let dinnerLength=$("#dinnerFoods li").length;
    }

    if (foodName.length<=0 || foodName.length>50){
        swal('', "음식 이름은 길이가 1이상 50이하여야 합니다.", "error");
        return;
    }

    $(ulName).append("<li>" +
        foodName +
        "<span class='close'>x</span></li>"
        + "</li>");
    $(".close").attr('onclick', 'closeList(this)');

}


function postDiary() {
    console.log("post");
}

function goToBack() {
    window.location.href = "/";
}

$(document).ready(function () {
    $("#breakFastFoodsAddBtn").attr('onclick', 'addFood(this)');
    $("#lunchFoodsAddBtn").attr('onclick', 'addFood(this)');
    $("#dinnerFoodsAddBtn").attr('onclick', 'addFood(this)');
    $("#createBtn").attr('onclick', 'postDiary()');
    $("#cancelBtn").attr('onclick', 'goToBack()');
});
