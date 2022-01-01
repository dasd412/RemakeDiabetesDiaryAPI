package com.dasd412.remake.api.config.auth;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

//Authentication 객체에 넣기 위한 래퍼 객체
//사용자 정의 UserDetails 와 OAuth 로그인용 OAuth2User 를 모두 implements 하였기 때문에
//Authentication 객체에 두 타입 모두로 인식가능해졌다.
public class PrincipalDetails implements UserDetails, OAuth2User {

    //실제 엔티티를 참조함.
    private final Writer writer;

    private Map<String,Object>oauthAttributes;

    //사용자 정의 일반 인증 시 사용되는 생성자
    public PrincipalDetails(Writer writer) {
        this.writer = writer;
    }

    //OAuth 인증 시 사용되는 생성자
    public PrincipalDetails(Writer writer, Map<String, Object> oauthAttributes) {
        this.writer = writer;
        this.oauthAttributes = oauthAttributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add((GrantedAuthority) () -> writer.getRole().name());
        return collection;
    }

    public Writer getWriter() {
        return writer;
    }

    @Override
    public String getPassword() {
        return writer.getPassword();
    }

    @Override
    public String getUsername() {
        return writer.getName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    //Oauth2User
    @Override
    public Map<String, Object> getAttributes() {
        return this.oauthAttributes;
    }

    @Override
    public String getName() {
        return null;
    }
}
