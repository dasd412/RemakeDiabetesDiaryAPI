package com.dasd412.remake.api.config.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

//AuthenticationFailureHandler 의 구현체를  WebSecurityConfigurerAdapter 상속체에 핸들러로 등록하면 로그인 실패 시 예외를 핸들링해 줄 수 있다.
//그리고 SimpleUrlAuthenticationFailureHandler 는 AuthenticationFailureHandler 구현체다.
public class LoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    //httpServletRequest -> request 에 대한 정보 , httpServletResponse -> response 에 대해 설정할 수 있는 변수
    //AuthenticationException e -> 로그인 실패 시 예외에 대한 정보를 담고 있음.
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        logger.info("login fail handler");

        String errorMessage;
        if (e instanceof BadCredentialsException || e instanceof InternalAuthenticationServiceException) {
            errorMessage = "아이디 또는 비밀번호가 맞지 않습니다.";
        } else if (e instanceof UsernameNotFoundException) {
            errorMessage = "존재하지 않는 아이디 입니다.";
        } else {
            logger.info("exception is  : " + e);
            errorMessage = "알 수 없는 이유로 로그인이 안되고 있습니다.";
        }
        logger.info(errorMessage);

        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
        setDefaultFailureUrl("/loginForm?error=true&exception=" + errorMessage);
        super.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
    }


}
