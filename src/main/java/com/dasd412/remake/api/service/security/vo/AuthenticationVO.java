package com.dasd412.remake.api.service.security.vo;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface AuthenticationVO {

    Writer makeEntityWithPasswordEncode(EntityId<Writer, Long> nextIdOfWriter, BCryptPasswordEncoder bCryptPasswordEncoder);

    String getName();

    String getEmail();

    Role getRole();

    String getProvider();

    String getProviderId();
}
