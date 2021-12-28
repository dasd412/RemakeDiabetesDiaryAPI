package com.dasd412.remake.api.config;

import com.dasd412.remake.api.domain.diary.writer.Role;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/diary/**").authenticated()// api/diary/ 로 시작하는 url 은 인증 필요
//                .antMatchers("/api/admin/**").access()
                .anyRequest().permitAll() // 그외에는 다 허용
                .and()
                .formLogin()// 로그인이 필요하면
                .loginPage("/loginForm");// loginForm 뷰로 이동.
    }
}
