package com.dasd412.remake.api.domain.diary.diabetesDiary;

import com.dasd412.remake.api.domain.diary.EntityId;
import com.dasd412.remake.api.domain.diary.diet.Diet;
import com.dasd412.remake.api.domain.diary.writer.Writer;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;

@Entity
@Table(name = "DiabetesDiary", uniqueConstraints = @UniqueConstraint(columnNames = {"diary_id"}))
@IdClass(DiabetesDiaryId.class)
public class DiabetesDiary {
    //식별 관계이므로 복합키 사용.
    @Id
    @Column(name = "diary_id", columnDefinition = "bigint default 0")
    private Long diaryId;

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
    @OneToMany(mappedBy = "diary",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private final Set<Diet> dietList = new HashSet<>();

    public DiabetesDiary() {
    }

    public DiabetesDiary(EntityId<DiabetesDiary, Long> diabetesDiaryEntityId, Writer writer, int fastingPlasmaGlucose, String remark, LocalDateTime writtenTime) {
        checkArgument(fastingPlasmaGlucose >=0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        checkArgument(remark.length() <= 500, "remark length should be lower than 501");
        this.diaryId = diabetesDiaryEntityId.getId();
        this.writer = writer;
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
        this.remark = remark;
        this.writtenTime = writtenTime;
    }

    public Long getId() {
        return diaryId;
    }

    public int getFastingPlasmaGlucose() {
        return fastingPlasmaGlucose;
    }

    private void modifyFastingPlasmaGlucose(int fastingPlasmaGlucose) {
        checkArgument(fastingPlasmaGlucose >= 0 && fastingPlasmaGlucose <= 1000, "fastingPlasmaGlucose must be between 0 and 1000");
        this.fastingPlasmaGlucose = fastingPlasmaGlucose;
    }

    public String getRemark() {
        return remark;
    }

    private void modifyRemark(String remark) {
        checkArgument(remark.length() <= 500, "remark length should be lower than 501");
        this.remark = remark;
    }

    public LocalDateTime getWrittenTime() {
        return writtenTime;
    }

    public List<Diet> getDietList() {
        return new ArrayList<>(dietList);
    }

    public void addDiet(Diet diet) {
        this.dietList.add(diet);
        //무한 루프 체크
        if (diet.getDiary() != this) {
            diet.makeRelationWithDiary(this);
        }
    }

    public Writer getWriter() {
        return writer;
    }

    /*
    복합키와 관련된 메서드이므로 엔티티 관계 설정이후엔 호출하면 안된다.
     */
    public void makeRelationWithWriter(Writer writer) {
        this.writer = writer;
        if (!writer.getDiaries().contains(this)) {
            writer.getDiaries().add(this);
        }
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("id", diaryId)
                .append("writerId", writer)
                .append("fpg", fastingPlasmaGlucose)
                .append("remark", remark)
                .append("written time", writtenTime)
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(writer, diaryId);
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
        return Objects.equals(this.writer, target.writer) && Objects.equals(this.diaryId, target.diaryId);
    }

    public void update(int fastingPlasmaGlucose, String remark) {
        modifyFastingPlasmaGlucose(fastingPlasmaGlucose);
        modifyRemark(remark);
    }

    //연관 관계 제거 시에만 사용
    public void removeDiet(Diet diet) {
        checkArgument(this.dietList.contains(diet), "this diary dose not have the diet");
        this.dietList.remove(diet);
    }
}
