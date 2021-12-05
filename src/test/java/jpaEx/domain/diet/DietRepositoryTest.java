package jpaEx.domain.diet;

import jpaEx.domain.food.FoodRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
@Profile("test")
public class DietRepositoryTest {
    @Autowired
    DietRepository dietRepository;

    @After
    public void clean(){
        dietRepository.deleteAll();
    }

    @Transactional
    @Test
    public void saveDiet(){
       //given
       Diet diet=new Diet(EatTime.BreakFast,100);
        dietRepository.save(diet);
       //when
        Diet found=dietRepository.findAll().get(0);

       //then
        assertThat(found).isEqualTo(diet);
    }



}