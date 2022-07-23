package com.dasd412.remake.api.service.security.vo;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

public class OAuth2UserVO implements AuthenticationVO{

    private final String name;
    private final String email;
    private final String password;
    private final Role role;
    private final String provider;
    private final String providerId;

    @Builder
    private OAuth2UserVO(String name, String email, String password, Role role, String provider, String providerId) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    @Override
    public Writer makeEntityWithPasswordEncode(EntityId<Writer, Long> entityId, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Writer.builder()
                .writerEntityId(entityId)
                .name(this.name)
                .email(this.email)
                .password(bCryptPasswordEncoder.encode(this.password))
                .role(this.getRole())
                .provider(this.provider)
                .providerId(this.getProviderId())
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, role, provider, providerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        OAuth2UserVO target = (OAuth2UserVO) obj;
        return Objects.equals(this.name, target.name)
                && Objects.equals(this.email, target.email)
                && Objects.equals(this.password, target.password)
                && Objects.equals(this.role, target.role)
                && Objects.equals(this.provider, target.provider)
                && Objects.equals(this.providerId, target.providerId);
    }
    @Override
    public String getName() {
        return name;
    }
    @Override
    public String getEmail() {
        return email;
    }
    @Override
    public Role getRole() {
        return role;
    }
    @Override
    public String getProvider() {
        return provider;
    }
    @Override
    public String getProviderId() {
        return providerId;
    }

}

