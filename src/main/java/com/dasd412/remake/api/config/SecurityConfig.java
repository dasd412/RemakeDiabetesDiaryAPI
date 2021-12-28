package com.dasd412.remake.api.config;

import com.dasd412.remake.api.domain.diary.writer.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(); //<- 비밀번호 암호화를 해준다.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/api/diary/**").authenticated()// api/diary/ 로 시작하는 url 은 인증 필요
                .anyRequest().permitAll() // 그외에는 다 허용
                .and()
                .formLogin()// 로그인이 필요하면
                .loginPage("/loginForm");// loginForm 뷰로 이동.
    }
}
