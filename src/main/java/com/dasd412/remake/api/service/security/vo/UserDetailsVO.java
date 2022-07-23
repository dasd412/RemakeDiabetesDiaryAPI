package com.dasd412.remake.api.service.security.vo;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import lombok.Builder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Objects;

public class UserDetailsVO implements AuthenticationVO {

    private final String name;
    private final String email;
    private final String password;
    private final Role role;

    @Builder
    private UserDetailsVO(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Writer makeEntityWithPasswordEncode(EntityId<Writer, Long> entityId, BCryptPasswordEncoder bCryptPasswordEncoder) {
        return Writer.builder()
                .writerEntityId(entityId)
                .name(this.name)
                .email(this.email)
                .password(bCryptPasswordEncoder.encode(this.password))
                .role(this.getRole())
                .provider(null)
                .providerId(null)
                .build();
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password, role);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UserDetailsVO target = (UserDetailsVO) obj;
        return Objects.equals(this.name, target.name)
                && Objects.equals(this.email, target.email)
                && Objects.equals(this.password, target.password)
                && Objects.equals(this.role, target.role);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public Role getRole() {
        return this.role;
    }

    @Override
    public String getProvider() {
        // sql 단에서 exist(null)할 수 있으므로 Optional로 감싸지 않는다.
        return null;
    }

    @Override
    public String getProviderId() {
        // sql 단에서 exist(null)할 수 있으므로 Optional로 감싸지 않는다.
        return null;
    }


}
