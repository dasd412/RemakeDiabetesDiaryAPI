package jpaEx.domain.diary;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;
//복합키 엔티티의 Id를 생성할 때, 타입 정확성을 위해 만든 보조 클래스
public class EntityId<R,Long> {
    /*
    R에는 엔티티 객체의 클래스 정보를 넣고 Long 에는 해당 엔티티의 식별자를 넣습니다.
     */
    private final Class<R> reference;
    private final Long id;

    /*생성자를 private 으로 잠그고 정적 팩토리 메소드 of()를 활용하였다.
     왜냐하면 이 클래스는 <R,Long> 형인자 자료형 객체인데 생성자로 만들면 타이핑하기 힘들기 때문이다.
     */
    private EntityId(Class<R> reference, Long id) {
        this.reference = reference;
        this.id = id;
    }

    public static <R, Long> EntityId<R, Long> of(Class<R> reference, Long id) {
        //입력 값 검증은 모델 단에서 하는게 효율적이다.
        checkNotNull(reference,"entity reference must be provided");
        checkNotNull(id,"entity id must be provided");

        return new EntityId<>(reference, id);
    }


    public Long getId() {
        return id;
    }
    @Override
    public int hashCode() {//클래스 참조 값을 해싱한 값 리턴.
        return Objects.hash(reference);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        EntityId<?, ?> target = (EntityId<?, ?>) obj;
        return Objects.equals(reference, target.reference) && Objects
                .equals(id, target.id);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("reference", reference.getSimpleName())
                .append("entityId", id)
                .toString();
    }
}
