package jpaEx.domain.diet;

import jpaEx.domain.food.Food;
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
public class DietRepositoryTest {
    @Autowired
    DietRepository dietRepository;

    @Autowired
    FoodRepository foodRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @After
    public void clean(){
        //외래키를 갖고 있는 food 부터 삭제해야 외래키 참조 오류가 발생하지 않는다.
        foodRepository.deleteAll();
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
            foodRepository.save(food);
            diet.addFood(food);
        }

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);
        List<Food> foundFood=foodRepository.findAll();
        //then
        assertThat(foundDiet).isEqualTo(diet);
        for (int i=0;i<foundFood.size();i++){
            assertThat(foundFood.get(i)).isEqualTo(foodList.get(i));
            assertThat(foundDiet.getFoodList().get(i)).isEqualTo(foodList.get(i));
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
            foodRepository.save(food);
            diet.addFood(food);
        }

        dietRepository.save(diet);

        //when
        Diet foundDiet=dietRepository.findAll().get(0);
        foundDiet.getFoodList().remove(juice);

        List<Food> foundFood=foodRepository.findAll();
        //then
        assertThat(foundDiet).isEqualTo(diet);
        assertThat(foundDiet.getFoodList().size()).isEqualTo(2);
        assertThat(foundFood.size()).isEqualTo(2);

        for (int i=0;i<foundFood.size();i++){
            logger.info(foundFood.get(i).getFoodName());
            logger.info(foundDiet.getFoodList().get(i).getFoodName());
        }
    }
}