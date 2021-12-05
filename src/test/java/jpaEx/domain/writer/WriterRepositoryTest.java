package jpaEx.domain.writer;

import jpaEx.domain.diary.DiabetesDiaryRepository;
import jpaEx.domain.diet.DietRepository;
import jpaEx.domain.food.FoodRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class WriterRepositoryTest {
    @Autowired
    WriterRepository writerRepository;

    @Autowired
    DiabetesDiaryRepository diaryRepository;

    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    @Test
    public void saveWriter(){
        //todo 엔티티 연관관계랑 함께 잘 저장 되는지 확인하는 테스트 수행 필요
    }

}