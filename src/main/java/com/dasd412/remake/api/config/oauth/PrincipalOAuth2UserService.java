package com.dasd412.remake.api.config.oauth;

import com.dasd412.remake.api.config.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.service.security.WriterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class PrincipalOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private final WriterService writerService;
    private final WriterRepository writerRepository;

    public PrincipalOAuth2UserService(WriterService writerService, WriterRepository writerRepository) {
        this.writerService = writerService;
        this.writerRepository = writerRepository;
    }

    //OAuth 리퀘스트 시 로드됨.
    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        String provider = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        String providerId = (String) oAuth2User.getAttributes().get("sub");
        String username = provider + "_" + providerId;
        String password = "diabetesdiaryapi";
        String email = (String) oAuth2User.getAttributes().get("email");
        Role role = Role.User;

        //만약 회원가입된 적 없으면, writerService 한테 회원 가입을 요청한다. 비밀 번호 인코딩도 해줌.
        Writer writer=writerRepository.findWriterByName(username)
                .orElseGet(()->writerService.saveWriterWithSecurity(username,email,password,role,provider,providerId));

        //리턴 객체는 스프링 시큐리티 세션 내의 Authentication 에 담기게 된다.
        return new PrincipalDetails(writer, oAuth2User.getAttributes());
    }
}
