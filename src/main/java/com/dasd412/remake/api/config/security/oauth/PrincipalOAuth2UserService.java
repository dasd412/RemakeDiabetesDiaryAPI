package com.dasd412.remake.api.config.security.oauth;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.config.security.oauth.provider.FaceBookUserInfo;
import com.dasd412.remake.api.config.security.oauth.provider.GitHubUserInfo;
import com.dasd412.remake.api.config.security.oauth.provider.GoogleUserInfo;
import com.dasd412.remake.api.config.security.oauth.provider.OAuth2UserInfo;
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

import java.util.Optional;

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

        OAuth2UserInfo oAuth2UserInfo = selectProvider(oAuth2User, oAuth2UserRequest).orElseThrow(() -> new IllegalStateException("등록된 provider 가 아닙니다."));
        String username = oAuth2UserInfo.getProvider() + "_" + oAuth2UserInfo.getProviderId();
        String password = "diabetesdiaryapi";
        Role role = Role.User;

        //만약 회원가입된 적 없으면, writerService 한테 회원 가입을 요청한다. 비밀 번호 인코딩도 해줌.
        Writer writer = writerRepository.findWriterByName(username)
                .orElseGet(() -> writerService.saveWriterWithSecurity(username, oAuth2UserInfo.getEmail(), password, role, oAuth2UserInfo.getProvider(), oAuth2UserInfo.getProviderId()));

        //리턴 객체는 스프링 시큐리티 세션 내의 Authentication 에 담기게 된다.
        return new PrincipalDetails(writer, oAuth2User.getAttributes());
    }

    private Optional<OAuth2UserInfo> selectProvider(OAuth2User oAuth2User, OAuth2UserRequest userRequest) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        switch (registrationId) {
            case "google":
                return Optional.of(new GoogleUserInfo(oAuth2User.getAttributes()));
            case "facebook":
                return Optional.of(new FaceBookUserInfo(oAuth2User.getAttributes()));
            case "github":
                return Optional.of(new GitHubUserInfo(oAuth2User.getAttributes()));
            default:
                return Optional.empty();
        }
    }
}
