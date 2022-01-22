/*
 * @(#)PrincipalDetailsService.java        1.0.1 2022/1/22
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.config.security.auth;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * "/login" 요청 시 인터셉트했는데 기본 Form Login인 인경우, UserDetails 를 Authentication 객체에 넣어주는 서비스 클래스.
 *
 * @author 양영준
 * @version 1.0.1 2022년 1월 22일
 */
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final WriterRepository writerRepository;

    public PrincipalDetailsService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    /**
     * @param username 사용자의 이름을 뜻한다. 주의할점: loginForm.mustache 의 username 과 파라미터 이름이 일치해야 에러가 발생하지 않는다.
     * @return 작성자 엔티티를 감싼 UserDetails. 스프링 시큐리티 내 Authentication 객체에 사용된다.
     * @throws UsernameNotFoundException 해당하는 사용자 이름이 없을 경우에 발생하는 예외.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Writer user = writerRepository.findWriterByName(username).orElseThrow(() -> new UsernameNotFoundException("해당 이름의 작성자가 없습니다."));
        /*
        이 메서드는 분명히 인자가 username 뿐이다. 그런데 비밀번호가 틀렸을 때도 인증이 제대로 되지 않았다고 알려준다.
        비밀번호 체크 코드가 없는데 어떻게 된 일 일까?
        이유는 DaoAuthenticationProvider 와 PrincipalDetails 때문이다.
        DaoAuthenticationProvider 는 UserDetails 구현체의 getPassword()를 호출하여 비밀번호가 맞는지 체크해준다.
        이 메서드의 리턴 값인 PrincipalDetails 는 UserDetails 구현체이기 때문에 getPassword()가 존재한다.
        따라서 이 메서드가 PrincipalDetails 를 Security 에게 던지면 DaoAuthenticationProvider 가 비밀번호를 직접 체크해주는 것이라 볼 수 있다.
         */
        return new PrincipalDetails(user);
    }
}
