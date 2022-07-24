/*
 * @(#)WriterService.java
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.service.security;

import com.dasd412.remake.api.controller.exception.DuplicateEmailException;
import com.dasd412.remake.api.controller.exception.DuplicateException;
import com.dasd412.remake.api.controller.exception.DuplicateUserNameException;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
import com.dasd412.remake.api.service.security.vo.AuthenticationVO;
import com.dasd412.remake.api.util.RegexChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.IntStream;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 사용자 회원 가입 로직을 수행하는 서비스 클래스
 */
@Service
public class WriterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;

    private static final char[] CHAR_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WriterService(WriterRepository writerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.writerRepository = writerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private EntityId<Writer, Long> getNextIdOfWriter() {
        Long writerId = writerRepository.findMaxOfId();
        if (writerId == null) {
            writerId = 0L;
        }
        return EntityId.of(Writer.class, writerId + 1);
    }

    private String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    @Transactional
    public Writer saveWriterWithSecurity(AuthenticationVO vo) throws DuplicateException {
        logger.info("join writer with security");

        if (writerRepository.existsName(vo.getName()) == Boolean.TRUE) {
            throw new DuplicateUserNameException("이미 존재하는 회원 이름입니다.");
        }

        if (writerRepository.existsEmail(vo.getEmail(), vo.getProvider()) == Boolean.TRUE) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        Writer writer = vo.makeEntityWithPasswordEncode(getNextIdOfWriter(),bCryptPasswordEncoder);

        writerRepository.save(writer);

        return writer;
    }


    @Transactional
    public void withdrawWriter(EntityId<Writer, Long> writerId) {
        logger.info("withdraw writer !!");
        checkNotNull(writerId, "writerId must be provided");
        writerRepository.bulkDeleteWriter(writerId.getId());
    }

    @Transactional
    public String issueNewPassword() {
        StringBuilder tempPassword = new StringBuilder();

        IntStream.range(0, 10).forEach(i ->
                tempPassword.append(CHAR_ARRAY[(int) (CHAR_ARRAY.length * Math.random())]));

        return tempPassword.toString();
    }

    @Transactional
    public void updateWithTempPassword(String email, String userName, String tempPassWord) {
        checkArgument(RegexChecker.isRightEmail(email), "String must be pattern of email!!");
        writerRepository.updateWithTempPassword(email, userName, this.encodePassword(tempPassWord));
    }

    /**
     * @param rawPassword 사용자가 입력한 새 비밀 번호 (인코딩 되야 함!)
     */
    @Transactional
    public void updatePassword(EntityId<Writer, Long> writerId, String rawPassword) {
        logger.info("update password!");
        checkNotNull(writerId, "writerId must be provided");
        /* 반드시 비밀번호를 인코딩해서 파라미터로 넘겨줘야 한다! */
        writerRepository.updatePassword(writerId.getId(), this.encodePassword(rawPassword));
    }
}
