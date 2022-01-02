package com.dasd412.remake.api.domain.diary.writer;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diabetesDiary.DiabetesDiary;

import javax.persistence.*;
import java.util.*;

import lombok.Builder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "Writer", uniqueConstraints = @UniqueConstraint(columnNames = {"writer_id", "name"}))
public class Writer {
    @Id
    @Column(name = "writer_id", columnDefinition = "bigint default 0", nullable = false, unique = true)
    private Long writerId;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    //OAuth provider
    private String provider;

    //OAuth provider id
    private String providerId;

    //연관된 엔티티의 컬렉션을 로딩하는 것은 비용이 너무 많이 드므로 지연 로딩.
    @OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<DiabetesDiary> diaries = new HashSet<>();

    public Writer() {
    }

    //시큐리티 없이 사용할 때 쓰인 생성자.
    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, Role role) {
        this(writerEntityId, name, email, null, role);
    }

    public Writer(EntityId<Writer, Long> writerEntityId, String name, String email, String password, Role role) {
        checkArgument(name.length() > 0 && name.length() <= 50, "name should be between 1 and 50");
        this.writerId = writerEntityId.getId();
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    //시큐리티 에 쓰이는 생성자
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

    public void addDiary(DiabetesDiary diary) {
        this.diaries.add(diary);
        //무한 루프 방지
        if (diary.getWriter() != this) {
            diary.makeRelationWithWriter(this);
        }
    }

    //연관 관계 제거 시에만 사용
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
