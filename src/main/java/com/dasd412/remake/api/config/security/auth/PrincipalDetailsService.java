package com.dasd412.remake.api.config.security.auth;

import com.dasd412.remake.api.config.security.dto.SessionWriter;
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
        return new PrincipalDetails(user);
    }
}
