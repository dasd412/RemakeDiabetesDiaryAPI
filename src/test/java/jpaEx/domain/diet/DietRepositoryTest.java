package jpaEx.domain.diet;

import jpaEx.domain.food.Food;

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
public class DietRepositoryTest {
    @Autowired
    DietRepository dietRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Transactional
    @Test
    public void saveFood(){
        //given
        Diet diet=new Diet(EatTime.Lunch,100,null);

        Food pizza=new Food("pizza",diet);
        diet.addFood(pizza);

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);

        //then
        assertThat(foundDiet).isEqualTo(diet);
        assertThat(foundDiet.getFoodList().get(0)).isEqualTo(pizza);
        assertThat(foundDiet.getFoodList().get(0).getFoodName()).isEqualTo(pizza.getFoodName());
        assertThat(foundDiet.getFoodList().get(0)).isEqualTo(pizza);
        assertThat(foundDiet.getFoodList().get(0).getFoodName()).isEqualTo(pizza.getFoodName());

        //음식으로부터 혈당을 알아낼 수 있다.
        assertThat(foundDiet.getFoodList().get(0).getDiet()).isEqualTo(foundDiet);
        assertThat(foundDiet.getFoodList().get(0).getDiet().getBloodSugar()).isEqualTo(diet.getBloodSugar());
        logger.info(foundDiet.getFoodList().get(0).toString());
    }

    @Transactional
    @Test
    public void saveFoodsInDiet(){
        //given
        Diet diet=new Diet(EatTime.Lunch,100,null);

        List<Food>foodList=new ArrayList<>();
        Food pizza=new Food("pizza",diet);
        Food juice=new Food("juice",diet);
        Food iceCream=new Food("iceCream",diet);

        foodList.add(pizza);
        foodList.add(juice);
        foodList.add(iceCream);

        for (Food food : foodList){
            diet.addFood(food);
        }

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);

        //then
        assertThat(foundDiet).isEqualTo(diet);
        for (int i=0;i<foundDiet.getFoodList().size();i++){
            assertThat(foundDiet.getFoodList().get(i)).isEqualTo(foodList.get(i));
            logger.info(foundDiet.getFoodList().get(i).toString());
        }
    }

    @Transactional
    @Test
    public void deleteFoodsInDiet(){
        //given
        Diet diet=new Diet(EatTime.Lunch,100,null);

        List<Food>foodList=new ArrayList<>();
        Food pizza=new Food("pizza",diet);
        Food juice=new Food("juice",diet);
        Food iceCream=new Food("iceCream",diet);

        foodList.add(pizza);
        foodList.add(juice);
        foodList.add(iceCream);

        for (Food food : foodList){
            diet.addFood(food);
        }

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);
        foundDiet.getFoodList().remove(juice);

        //then
        assertThat(foundDiet).isEqualTo(diet);
        assertThat(foundDiet.getFoodList().size()).isEqualTo(2);

        for (int i=0;i<foundDiet.getFoodList().size();i++){
            logger.info(foundDiet.getFoodList().get(i).getFoodName());
        }
    }
}