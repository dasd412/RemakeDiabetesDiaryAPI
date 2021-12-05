package jpaEx.domain.diary;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;
import jpaEx.domain.writer.Writer;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name="DiabetesDiary")
public class DiabetesDiary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="diary_id")
    private Long id;

    private int fastingPlasmaGlucose;

    private String remark;

    private LocalDateTime writtenTime;

    //일 대 다 단방향 관계의 경우 외래키가 없는 일쪽에서 외래키를 관리하므로 Update 쿼리를 날려야 하는 추가 비용이 존재한다. 따라서 다 대 일 양방향 매핑으로 바꿔야 한다.
    //양방향이므로 연관관계의 주인을 정해야 한다. "일"에 해당하는 엔티티는 외래키가 없으므로 연관관계의 주인이 아니다. 이를 명시하기 위해 mapped by를 사용한다.
    @OneToMany(mappedBy = "diary")
    private List<Diet>dietList=new ArrayList<>();

    //Writer와 일대다 양방향 관계이며, 연관관계의 주인이다.
    @ManyToOne
    @JoinColumn(name="writer_id")
    private Writer writer;

    protected DiabetesDiary(){}

    public DiabetesDiary(int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        this(fastingPlasmaGlucose,remark,writtenTime,null);
    }

    public DiabetesDiary(int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime, Writer writer) {
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.writtenTime = writtenTime;
        this.writer = writer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    public void modifyFastingPlasmaGlucose(int fastingPlasmaGlucose) {
        checkArgument(fastingPlasmaGlucose>0,"fastingPlasmaGlucose must be positive number");
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    public void modifyRemark(String remark) {
        checkArgument(remark.length()>0 && remark.length()<=500,"remark length should be between 1 and 500");
        this.remark = remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

    public void setWrittenTime(LocalDateTime writtenTime) {
        this.writtenTime = writtenTime;
    }

    public List<Diet> getDietList() {
        return dietList;
    }

    public void setDietList(List<Diet> dietList) {
        this.dietList = dietList;
    }

    public void addDiet(Diet diet){
        this.dietList.add(diet);
        //무한 루프 체크
        if(diet.getDiary()!=this){
            diet.setDiary(this);
        }
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
        if(!writer.getDiaries().contains(this)){
            writer.getDiaries().add(this);
        }
    }
}
