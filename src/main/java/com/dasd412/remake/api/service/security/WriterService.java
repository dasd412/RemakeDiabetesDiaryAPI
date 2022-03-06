/*
 * @(#)WriterService.java        1.1.2 2022/3/6
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
import com.dasd412.remake.api.domain.diary.writer.Role;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import com.dasd412.remake.api.domain.diary.writer.WriterRepository;
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
 *
 * @author 양영준
 * @version 1.1.2 2022년 3월 6일
 */
@Service
public class WriterService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WriterRepository writerRepository;

    private static final char[] CHAR_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
            'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    /**
     * 패스워드 암호화 객체
     */
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public WriterService(WriterRepository writerRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.writerRepository = writerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * 작성자 id 생성 메서드 (트랜잭션 필수). 시큐리티 적용 후에는 WriterService가 담당한다.
     *
     * @return 래퍼로 감싸진 작성자 id
     */
    private EntityId<Writer, Long> getNextIdOfWriter() {
        Long writerId = writerRepository.findMaxOfId();
        if (writerId == null) {
            writerId = 0L;
        }
        return EntityId.of(Writer.class, writerId + 1);
    }

    /**
     * @param rawPassword 사용자 입력 비밀번호
     * @return 암호화된 비밀번호
     */
    private String encodePassword(String rawPassword) {
        return bCryptPasswordEncoder.encode(rawPassword);
    }

    /**
     * 회원 가입 메서드
     *
     * @param name       유저 네임
     * @param email      이메일 (깃헙의 경우 nullable)
     * @param password   비밀 번호 (OAuth의 경우 null)
     * @param role       권한
     * @param provider   OAuth 제공자
     * @param providerId OAuth 제공자 식별 값
     * @return 회원 가입된 작성자
     * @throws DuplicateException 속성이 중복일 경우 던져지는 예외
     */
    @Transactional
    public Writer saveWriterWithSecurity(String name, String email, String password, Role role, String provider, String providerId) throws DuplicateException {
        logger.info("join writer with security");

        /* 중복 체크 */
        if (writerRepository.existsName(name) == Boolean.TRUE) {
            throw new DuplicateUserNameException("이미 존재하는 회원 이름입니다.");
        }

        if (writerRepository.existsEmail(email, provider) == Boolean.TRUE) {
            throw new DuplicateEmailException("이미 존재하는 이메일입니다.");
        }

        /* 비밀 번호 암호화 */
        String encodedPassword = encodePassword(password);

        /* 실제 회원 가입 처리 */
        Writer writer = Writer.builder()
                .writerEntityId(getNextIdOfWriter()).name(name)
                .email(email).password(encodedPassword).role(role)
                .provider(provider).providerId(providerId)
                .build();
        writerRepository.save(writer);

        return writer;
    }

    /**
     * 작성자 회원 탈퇴
     *
     * @param writerId 래퍼로 감싸진 작성자 id
     */
    @Transactional
    public void withdrawWriter(EntityId<Writer, Long> writerId) {
        logger.info("withdraw writer !!");
        checkNotNull(writerId, "writerId must be provided");
        writerRepository.bulkDeleteWriter(writerId.getId());
    }

    /**
     * @return 새로운 임시 비밀 번호
     */
    @Transactional
    public String issueNewPassword() {
        StringBuilder tempPassword = new StringBuilder();

        IntStream.range(0, 10).forEach(i ->
                tempPassword.append(CHAR_ARRAY[(int) (CHAR_ARRAY.length * Math.random())]));

        return tempPassword.toString();
    }

    /**
     * @param email        사용자 email
     * @param userName     사용자 id (unique == true)
     * @param tempPassWord 새로 발급 받은 임시 비밀 번호
     */
    @Transactional
    public void updateTempPassword(String email, String userName, String tempPassWord) {
        checkArgument(RegexChecker.isRightEmail(email), "String must be pattern of email!!");
        writerRepository.updateTempPassword(email, userName, this.encodePassword(tempPassWord));
    }

    /**
     * 비밀 번호 변경 메서드
     *
     * @param writerId    래퍼로 감싸진 작성자 id
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
