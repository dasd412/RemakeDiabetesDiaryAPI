package refactoringAPI.domain.diary.diabetesDiary;

import com.querydsl.jpa.impl.JPAQueryFactory;
import refactoringAPI.domain.diary.writer.Writer;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class DiaryRepositoryImpl implements DiaryRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

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

    public DiaryRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Long findCountOfId() {
        return jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).fetchCount();
    }

    @Override
    public Long findMaxOfId() {
        return jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).select(QDiabetesDiary.diabetesDiary.diaryId.max()).fetchOne();
    }

    @Override
    public Optional<Writer> findWriterOfDiary(Long diaryId) {
        //@Query(value = "SELECT diary.writer FROM DiabetesDiary diary WHERE diary.diaryId = :diary_id")
        return Optional.ofNullable(jpaQueryFactory.from(QDiabetesDiary.diabetesDiary).select(QDiabetesDiary.diabetesDiary.writer).where(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId)).fetchOne());
    }

    @Override
    public List<DiabetesDiary> findDiabetesDiariesOfWriter(Long writerId) {
        //@Query(value = "FROM DiabetesDiary diary WHERE diary.writer.writerId = :writer_id")
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary).where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId)).fetch();
    }

    @Override
    public Optional<DiabetesDiary> findOneDiabetesDiaryByIdInWriter(Long writerId, Long diaryId) {
        //@Query(value = "FROM DiabetesDiary diary WHERE diary.writer.writerId = :writer_id AND diary.diaryId = :diary_id")
        return Optional.ofNullable(jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary).where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.diaryId.eq(diaryId))).fetchOne());
    }

    @Override
    public List<DiabetesDiary> findDiaryBetweenTime(Long writerId, LocalDateTime startDate, LocalDateTime endDate) {
        //@Query(value = "SELECT diary FROM DiabetesDiary diary  WHERE diary.writer.writerId = :writer_id AND diary.writtenTime BETWEEN :startDate AND :endDate")
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary).where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.writtenTime.between(startDate, endDate))).fetch();
    }

    @Override
    public List<DiabetesDiary> findFpgHigherOrEqual(Long writerId, int fastingPlasmaGlucose) {
        //@Query(value = "SELECT diary FROM DiabetesDiary diary WHERE diary.writer.writerId = :writer_id AND diary.fastingPlasmaGlucose >= :bloodSugar")
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary).where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.goe(fastingPlasmaGlucose))).fetch();
    }

    @Override
    public List<DiabetesDiary> findFpgLowerOrEqual(Long writerId, int fastingPlasmaGlucose) {
        //@Query(value = "SELECT diary FROM DiabetesDiary diary  WHERE diary.writer.writerId = :writer_id AND diary.fastingPlasmaGlucose <= :bloodSugar")
        return jpaQueryFactory.selectFrom(QDiabetesDiary.diabetesDiary).where(QDiabetesDiary.diabetesDiary.writer.writerId.eq(writerId).and(QDiabetesDiary.diabetesDiary.fastingPlasmaGlucose.loe(fastingPlasmaGlucose))).fetch();
    }
}
