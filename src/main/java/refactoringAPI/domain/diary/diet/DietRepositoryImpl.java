package refactoringAPI.domain.diary.diet;


import com.querydsl.jpa.impl.JPAQueryFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DietRepositoryImpl implements DietRepositoryCustom {
    /*
    fetch : 조회 대상이 여러건일 경우. 컬렉션 반환
    fetchOne : 조회 대상이 1건일 경우(1건 이상일 경우 에러). generic 에 지정한 타입으로 반환
    fetchFirst : 조회 대상이 1건이든 1건 이상이든 무조건 1건만 반환. 내부에 보면 return limit(1).fetchOne() 으로 되어있음
    fetchCount : 개수 조회. long 타입 반환
    fetchResults : 조회한 리스트 + 전체 개수를 포함한 QueryResults 반환. count 쿼리가 추가로 실행된다.
    */

    /*
    부등호 표현식
    lt <
    loe <=
    gt >
    goe >=
    */
    private final JPAQueryFactory jpaQueryFactory;

    public DietRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findCountOfId() {
        return jpaQueryFactory.from(QDiet.diet).fetchCount();
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiet.diet).select(QDiet.diet.dietId.max()).fetchOne();
    }

    @Override
    public List<Diet> findDietsInDiary(Long writerId, Long diaryId) {
        //@Query(value = "SELECT diet FROM Diet as diet WHERE diet.diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id")
        return null;
    }

    @Override
    public Optional<Diet> findOneDietByIdInDiary(Long writerId, Long diaryId, Long dietId) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diary.writer.writerId = :writer_id AND diet.diary.diaryId = :diary_id AND  diet.dietId = :diet_id")
        return null;
    }

    @Override
    public List<Diet> findHigherThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diet.diary.writer.writerId = :writer_id AND diet.bloodSugar >= :blood_sugar AND diet.diary.writtenTime BETWEEN :startDate AND :endDate")
        return null;
    }

    @Override
    public List<Diet> findLowerThanBloodSugarBetweenTime(Long writerId, int bloodSugar, LocalDateTime startDate, LocalDateTime endDate) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diet.diary.writer.writerId = :writer_id AND diet.bloodSugar <= :blood_sugar AND diet.diary.writtenTime BETWEEN :startDate AND :endDate")
        return null;
    }

    @Override
    public List<Diet> findHigherThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diet.diary.writer.writerId = :writer_id AND diet.bloodSugar >= :blood_sugar AND diet.eatTime =:eat_time")
        return null;
    }

    @Override
    public List<Diet> findLowerThanBloodSugarInEatTime(Long writerId, int bloodSugar, EatTime eatTime) {
        //@Query(value = "SELECT diet FROM Diet diet WHERE diet.diary.writer.writerId = :writer_id AND diet.bloodSugar <= :blood_sugar AND diet.eatTime =:eat_time")
        return null;
    }

    @Override
    public double findAverageBloodSugarOfDiet(Long writerId) {
        //@Query(value="SELECT AVG(diet.bloodSugar) FROM Diet diet WHERE diet.diary.writer.writerId = :writer_id")
        return 0;
    }
}
