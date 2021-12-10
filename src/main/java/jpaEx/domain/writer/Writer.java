package jpaEx.domain.writer;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diary.DiabetesDiary;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name="Writer")
public class Writer extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="writer_id")
    private Long id;

    private String name;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    //연관된 엔티티의 컬렉션을 로딩하는 것은 비용이 너무 많이 드므로 지연 로딩.
    //orphanRemoval=true 로 지정하면, 부모 엔티티 컬렉션에서 자식 엔티티를 삭제할 때 참조가 끊어지면서 db 에도 삭제된다.
    //cascade = CascadeType.ALL 을 적용하면 이 엔티티에 적용된 작업이 연관된 다른 엔티티들에도 모두 적용이 전파된다.
    @OneToMany(mappedBy = "writer",orphanRemoval = true,cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private List<DiabetesDiary>diaries=new ArrayList<>();


    protected Writer(){}

    public Writer(String name, String email, Role role) {
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        checkArgument(name.length()>0 && name.length()<=50, "name should be between 1 and 50");
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        //CHECK Email
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public List<DiabetesDiary> getDiaries() {
        return diaries;
    }

    public void setDiaries(List<DiabetesDiary> diaries) {
        this.diaries = diaries;
    }

    public void addDiary(DiabetesDiary diary){
        this.diaries.add(diary);
        //무한 루프 방지
        if(diary.getWriter()!=this){
            diary.setWriter(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id",id)
                .append("name",name)
                .append("email",email)
                .append("role",role)
                .toString();
    }
}
