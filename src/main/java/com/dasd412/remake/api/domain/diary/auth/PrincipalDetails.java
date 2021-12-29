package com.dasd412.remake.api.domain.diary.auth;

import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

//Authentication 객체에 넣기 위한 래퍼 객체
public class PrincipalDetails implements UserDetails {

    //실제 엔티티를 참조함.
    private final Writer writer;

    public PrincipalDetails(Writer writer) {
        this.writer = writer;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return writer.getRole().name();
            }
        });
        return collection;
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
}
