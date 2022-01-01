package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WriterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WriterService(WriterRepository writerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.writerRepository = writerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //작성자 id 생성 메서드 (트랜잭션 필수)
    private EntityId<Writer, Long> getNextIdOfWriter() {
        Long count = writerRepository.findCountOfId();
        Long writerId;
        if (count == 0) {
            writerId = 0L;
        } else {
            writerId = writerRepository.findMaxOfId();
        }
        return EntityId.of(Writer.class, writerId + 1);
    }

    @Transactional
    public void saveWriterWithSecurity(String name, String email, String password, Role role) {
        logger.info("saveWriter");
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        Writer writer = new Writer(getNextIdOfWriter(), name, email, encodedPassword, role);
        writerRepository.save(writer);
    }
}
