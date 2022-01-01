
function submitSignup(){

    const userData={
        name:$("#username").val(),
        password:$("#password").val(),
        email:$("#email").val()
    };

    $.ajax({
        type:"POST",
        url:"/signup/user",
        dataType:'json',
        contentType:'application/json; charset=utf-8',
        data: JSON.stringify(userData)
    }).done(function(e){

        //회원가입 성공 시 처리
        if(e.success==true){
            swal('',"회원 가입 성공!","success");
        //실패 시 alert message
        }
        else{
            if(e.error.message=="duplicateName"){
                swal('',"중복된 이름이에요.","error");
            }
            else if(e.error.message=="duplicateEmail"){
                swal('',"중복된 이메일이에요.","error");
            }
        }

    });
}

$(document).ready(function () {
    $('#signupBtn').attr('onclick', 'submitSignup()');
});
