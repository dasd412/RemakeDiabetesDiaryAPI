package com.dasd412.remake.api.config.security.auth;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


// "/login" 요청 시 인터셉트해서 UserDetails 를 Authentication 객체에 넣어주는 서비스.
@Service
public class PrincipalDetailsService implements UserDetailsService {

    private final WriterRepository writerRepository;

    public PrincipalDetailsService(WriterRepository writerRepository) {
        this.writerRepository = writerRepository;
    }

    //loginForm.mustache 의 username 과 파라미터 이름이 일치해야 에러 안남.
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
