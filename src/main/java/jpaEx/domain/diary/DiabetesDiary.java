package jpaEx.domain.diary;

import jpaEx.domain.BaseTimeEntity;
import jpaEx.domain.diet.Diet;
import jpaEx.domain.writer.Writer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "DiabetesDiary")
@IdClass(DiabetesDiaryId.class)
public class DiabetesDiary extends BaseTimeEntity {
    //식별 관계이므로 복합키 사용.
    @Id
    @Column(name = "diary_id", columnDefinition = "bigint default 0 auto_increment")
    private Long id;

    //Writer 와 일대다 양방향 관계이며, 연관관계의 주인이다. 되도록이면 모든 연관 관계를 지연로딩으로 사용하는 것이 성능에 좋음.
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id")
    private Writer writer;

    @Column(name = "fpg")
    private int fastingPlasmaGlucose;

    private String remark;

    private LocalDateTime writtenTime;

    //일 대 다 단방향 관계의 경우 외래키가 없는 일쪽에서 외래키를 관리하므로 Update 쿼리를 날려야 하는 추가 비용이 존재한다. 따라서 다 대 일 양방향 매핑으로 바꿔야 한다.
    //양방향이므로 연관관계의 주인을 정해야 한다. "일"에 해당하는 엔티티는 외래키가 없으므로 연관관계의 주인이 아니다. 이를 명시하기 위해 mapped by를 사용한다.
    @OneToMany(mappedBy = "diary", orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final List<Diet> dietList = new ArrayList<>();

    public DiabetesDiary() {
    }

    public DiabetesDiary(Long id, Writer writer, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        this.id = id;
        this.writer = writer;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.writtenTime = writtenTime;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    public void modifyFastingPlasmaGlucose(int fastingPlasmaGlucose) {
        checkArgument(fastingPlasmaGlucose > 0, "fastingPlasmaGlucose must be positive number");
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    public void modifyRemark(String remark) {
        checkArgument(remark.length() > 0 && remark.length() <= 500, "remark length should be between 1 and 500");
        this.remark = remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

    public List<Diet> getDietList() {
        return dietList;
    }

    public void addDiet(Diet diet) {
        this.dietList.add(diet);
        //무한 루프 체크
        if (diet.getDiary() != this) {
            diet.setDiary(this);
        }
    }

    public Writer getWriter() {
        return writer;
    }

    public void modifyWriter(Writer writer) {
        this.writer = writer;
        if (!writer.getDiaries().contains(this)) {
            writer.getDiaries().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", id)
                .append("writerId", writer)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(writer, id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        DiabetesDiary target = (DiabetesDiary) obj;
        return Objects.equals(this.writer, target.writer) && Objects.equals(this.id, target.id);
    }
}
