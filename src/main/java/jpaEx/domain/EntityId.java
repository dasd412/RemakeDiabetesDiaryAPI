package jpaEx.domain;

//복합키 엔티티의 Id를 생성할 때, 타입 정확성을 위해 만든 보조 클래스
public class EntityId<T> {
    private final Long id;

    public EntityId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
