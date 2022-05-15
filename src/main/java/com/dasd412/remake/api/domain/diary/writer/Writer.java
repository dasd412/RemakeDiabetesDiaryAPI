/*
 * @(#)Writer.java        1.1.1 2022/2/27
 *
 * Copyright (c) 2022 YoungJun Yang.
 * ComputerScience, ProgrammingLanguage, Java, Pocheon-si, KOREA
 * All rights reserved.
 */

package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.BaseTimeEntity;
import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;

import javax.persistence.*;
import java.util.*;

import com.dasd412.remake.api.domain.diary.profile.Profile;
import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * 작성자 엔티티. 로그인 정보와 관련이 있다.
 *
 * @author 양영준
 * @version 1.2.2 2022년 5월 15일
 */
@Entity
@Table(name = "Writer", uniqueConstraints = @UniqueConstraint(columnNames = {"writer_id", "name"}))
public class Writer extends BaseTimeEntity {

    /**
     * 작성자의 식별자
     */
    @Id
    @Column(name = "writer_id", columnDefinition = "bigint default 0", nullable = false, unique = true)
    private Long writerId;

    /**
     * 작성자 이름
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * 작성자 이메일. github의 경우 null일 수 있다.
     */
    @Column(name = "email", nullable = false)
    private String email;

    /**
     * 작성자의 비밀 번호. OAuth 로그인의 경우 필요 없다.
     * Form Login 방식의 경우 암호화되서 db에 저장된다.
     */
    private String password;

    /**
     * 작성자의 권한
     */
    @Enumerated(EnumType.STRING)
    private Role role;

    /**
     * OAuth 로그인일 경우 provider가 누구인지
     */
    private String provider;

    /**
     * OAuth provider의 id
     */
    private String providerId;

    /**
     * 연관된 엔티티의 컬렉션을 로딩하는 것은 비용이 너무 많이 드므로 지연 로딩.
     */
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<DiabetesDiary> diaries = new HashSet<>();

    /**
     * Writer -> Profile 로의 단방향 참조 관계
     * 그리고 Writer에 외래키가 존재하므로 @JoinColumn 부착.
     */
    @OneToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Writer() {
    }

    /**
     * 시큐리티 없이 사용할 때 쓰인 생성자.
     *
     * @deprecated
     */
    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, Role role) {
        this(writerEntityId, name, email, null, role);
    }

    /**
     * @param writerEntityId EntityId로 감싸진 작성자 id
     * @param name           유저 네임
     * @param email          이메일
     * @param password       암호화된 비밀 번호
     * @param role           권한
     * @deprecated
     */
    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, String password, Role role) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.writerId = writerEntityId.getId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /**
     * 시큐리티 적용 이후 사용되는 생성자
     *
     * @param writerEntityId EntityId로 감싸진 작성자 id
     * @param name           유저 네임
     * @param email          이메일 (github의 경우 null일 수 있다.)
     * @param password       암호화된 비밀 번호 (OAuth 로그인의 경우 null과 마찬가지)
     * @param role           권한
     * @param provider       OAuth 제공자 (Form Login의 경우 null)
     * @param providerId     OAuth 제공자 식별자 (Form Login의 경우 null)
     */
    @Builder
    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, String password, Role role, String provider, String providerId) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.writerId = writerEntityId.getId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.provider = provider;
        this.providerId = providerId;
    }

    public Long getId() {
        return writerId;
    }

    public String getName() {
        return name;
    }

    private void modifyName(String name) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    private void modifyEmail(String email) {
        //CHECK Email
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    private void modifyRole(Role role) {
        this.role = role;
    }

    public List<DiabetesDiary> getDiaries() {
        return new ArrayList<>(diaries);
    }

    public String getPassword() {
        return password;
    }

    public String getProvider() {
        return provider;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    public void addDiary(DiabetesDiary diary) {
        this.diaries.add(diary);
        /* 무한 루프 방지 */
        if (diary.getWriter() != this) {
            diary.makeRelationWithWriter(this);
        }
    }

    /**
     * 연관 관계 제거 시에만 사용
     *
     * @param diary 작성자가 작성했던 혈당 일지
     */
    public void removeDiary(DiabetesDiary diary) {
        checkArgument(this.diaries.contains(diary), "this writer does not have the diary");
        this.diaries.remove(diary);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", writerId)
                .append("name", name)
                .append("email", email)
                .append("role", role)
                .append("provider", provider)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(writerId);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Writer target = (Writer) obj;
        return Objects.equals(this.writerId, target.writerId);
    }

    public void update(String name, String email, Role role) {
        modifyName(name);
        modifyEmail(email);
        modifyRole(role);
    }
}
