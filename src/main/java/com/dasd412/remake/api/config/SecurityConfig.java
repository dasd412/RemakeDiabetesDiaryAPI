package com.dasd412.remake.api.config;

import com.dasd412.remake.api.config.security.oauth.PrincipalOAuth2UserService;
import com.dasd412.remake.api.domain.diary.writer.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PrincipalOAuth2UserService principalOAuth2UserService;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder(); //<- 비밀번호 암호화를 해준다.
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();

        http.authorizeRequests()
                .antMatchers("/api/diary/**").hasRole(Role.User.name())
                .anyRequest().permitAll()
                .and()
                .formLogin()// 로그인이 필요하면
                .loginPage("/loginForm")// loginForm 뷰로 이동.
                .loginProcessingUrl("/login")// "/login" 요청이 오면 스프링 시큐리티가 인터셉트해서 대신 로그인 해줌.
                .defaultSuccessUrl("/")// 로그인 성공하면 이동하는 디폴트 url 설정.
                .and()
                .oauth2Login()//Oauth 로그인 역시 "/loginForm" 으로 이동하게 함.
                .loginPage("/loginForm")
                .userInfoEndpoint()
                .userService(principalOAuth2UserService);//Oauth 로그인 이후의 후처리 담당하는 객체. OAuth2UserService 구현체여야 한다.

        //로그아웃 성공 시 인덱스 페이지로 이동.
        http.logout().logoutSuccessUrl("/");
    }
}
