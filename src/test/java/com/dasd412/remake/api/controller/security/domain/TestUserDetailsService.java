package com.dasd412.remake.api.controller.security.domain;

import com.dasd412.remake.api.config.security.auth.PrincipalDetails;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.service.domain.SaveDiaryService;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Profile("test")
public class TestUserDetailsService implements UserDetailsService {

    public static final String USERNAME = "user@example.com";

    private Writer getUser() {
        return Writer.builder()
                .writerEntityId(EntityId.of(Writer.class, 1L))
                .name(USERNAME)
                .email(USERNAME)
                .password("test")
                .provider(null)
                .providerId(null)
                .role(Role.User)
                .build();
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        if (s.equals(USERNAME)) {
            return new PrincipalDetails(getUser());
        }
        return null;
    }
}
