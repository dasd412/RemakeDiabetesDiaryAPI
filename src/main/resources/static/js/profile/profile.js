/*
 * @(#)profile.js        1.1.2 2022/3/6
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, JavaScript, Pocheon-si, KOREA
 * All rights reserved.
 */

/**
 * 일지 작성 폼을 담당
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 6일
 */

const ProfileManipulator = {
    isModalOn: false,

    init: function () {
        const _this = this;
        $("#updateProfileBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });

        $("#profileModalDecideBtn").on('click', function () {
            _this.updateProfile();
        });
        $("#profileModalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#profileModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#profileModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    },
    updateProfile: function () {

        const data = {
            diabetesPhase: $("#phaseSelector option:selected").val()
        };

        $.ajax({
            type: 'PUT',
            url: '/profile/info',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function () {
            location.reload();
        });
    }

};

const WithDrawlManipulator = {

    isModalOn: false,

    init: function () {
        const _this = this;

        $("#withdrawalBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
        $("#withdrawalModalDecideBtn").on('click', function () {
            _this.withdraw();
        });
        $("#withdrawalModalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#withdrawalModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#withdrawalModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    },
    withdraw: function () {

        const withdrawalConfirmPlaceHolder=$("#withdrawConfirm").attr('placeholder');
        const withdrawalConfirm=$("#withdrawConfirm").val();

        console.log(withdrawalConfirm,withdrawalConfirmPlaceHolder);

        if(withdrawalConfirm!==withdrawalConfirmPlaceHolder){
            swal('', "자신의 이메일을 제대로 서명해주세요", 'error');
            return;
        }

        $.ajax({
            type: 'DELETE',
            url: "/profile/withdrawal",
            dataType: 'json',
            contentType: 'application/json; charset=utf-8'
        }).done(function () {
            window.location.href = "/";
        });
    }
};

const UpdatePasswordManipulator = {

    isModalOn: false,

    init: function () {
        const _this = this;
        $("#updatePasswordBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });

        $("#updatePasswordModalDecideBtn").on('click', function () {
            _this.changePassword();
        });

        $("#updatePasswordModalCloseBtn").on('click', function () {
            _this.changeVisibilityOfModal();
        });
    },
    changeVisibilityOfModal: function () {
        if (this.isModalOn === false) {
            $("#updatePasswordModal").attr("style", "display:block;");
            this.isModalOn = true;
        } else {
            $("#updatePasswordModal").attr("style", "display:none;");
            this.isModalOn = false;
        }
    },

    /*
    흠... 아래 메서드는 보안 요소 추가가 필요할 듯..?
     */
    changePassword: function () {
        const data = {
            password: $("#passwordInput").val(),
            passwordConfirm: $("#passwordConfirmInput").val()
        }

        if (data.password.length < 8 || data.password.length > 20 || data.passwordConfirm.length < 8 || data.passwordConfirm.length > 20) {
            swal('', "변경하고자 하는 비밀 번호는 길이가 8이상 20이하여야 합니다.", 'error');
            return;
        }

        $.ajax({
            type: 'PUT',
            url: '/profile/password',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function (e) {
            if (e.success === true) {
                swal('', e.response, 'success');
            } else {
                swal('', e.error.message, 'error');
            }
        });
    }
}

$(document).ready(function () {
    ProfileManipulator.init();
    UpdatePasswordManipulator.init();
    WithDrawlManipulator.init();
});