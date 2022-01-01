package com.dasd412.remake.api.controller.exception;

//OAuth 가 아닌 회원 가입 시 이메일이 중복되면 발생합니다.
public class DuplicateEmailException extends DuplicateException{
    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
