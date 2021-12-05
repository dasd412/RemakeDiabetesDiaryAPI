package jpaEx.domain.food;

import jpaEx.domain.diet.Diet;
import jpaEx.domain.diet.DietRepository;
import jpaEx.domain.diet.EatTime;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;

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
public class FoodRepositoryTest {
    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    @After
    public void clean(){
        //외래키를 갖고 있는 food부터 삭제해야 외래키 참조 오류가 발생하지 않는다.
        foodRepository.deleteAll();
        dietRepository.deleteAll();
    }

    @Transactional
    @Test
    public void saveFood(){
        //given
        Diet diet=new Diet(EatTime.Lunch,100,null);

        Food pizza=new Food("pizza",diet);
        foodRepository.save(pizza);

        diet.addFood(pizza);

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);
        Food foundFood=foodRepository.findAll().get(0);
        //then
        assertThat(foundDiet).isEqualTo(diet);
        assertThat(foundFood).isEqualTo(pizza);
        assertThat(foundFood.getFoodName()).isEqualTo(pizza.getFoodName());
        assertThat(foundDiet.getFoodList().get(0)).isEqualTo(pizza);
        assertThat(foundDiet.getFoodList().get(0).getFoodName()).isEqualTo(pizza.getFoodName());

        //음식으로부터 혈당을 알아낼 수 있다.
        assertThat(foundFood.getDiet()).isEqualTo(foundDiet);
        assertThat(foundFood.getDiet().getBloodSugar()).isEqualTo(diet.getBloodSugar());

    }

}