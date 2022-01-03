function validateId(username) {
    return username.length > 0;
}

function validatePw(password) {
    return password.length >= 7;
}

$(document).ready(function () {
    $("#login-form").on('submit', function (e) {
        e.preventDefault();

        const username = $("#username").val();
        const password = $("#password").val();
        if (validateId(username) === false) {
            swal('', "공백으로 된 이름이에요.", "error");
            return;
        }
        if (validatePw(password) === false) {
            swal('', "비밀번호는 7자 이상이여야 해요.", "error");
            return;
        }
        this.submit();
    });
});