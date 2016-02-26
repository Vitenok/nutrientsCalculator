package com.iti.foodCalculator.service;

import com.iti.foodCalculator.dao.DayFoodPlanDAO;
import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.dao.UserDAO;
import com.iti.foodCalculator.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath:core-application-context.xml")
public class DataValidityTest {

    private static final Logger LOG = LogManager.getLogger(DataValidityTest.class);

    @Autowired
    DayFoodPlanDAO dayFoodPlanDAO;

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    UserDAO userDAO;

    @Test
    public void testSomething() {
        User user = new User("Test3", "GOOGLE", "10486224022");
        userDAO.save(user);

        List<Product> products = productsDAO.findByNames(new ArrayList() {{
            add("Egg white, raw");
            add("Chicken, breast, with skin, raw");
            add("Beef, minced meat, max 6 % fat, raw");
            add("Rice, brown, long-grain, uncooked");
            add("Crisp bread, wholemeal flour, rye, Husman");
            add("Cheese, hard, Cheddar");
        }});

//        User me = userDAO.find("Petro Krasnomovets", "GOOGLE", "104862240226572851476");

        ProductPlan pp11 = new ProductPlan(products.get(4), 10.0);
        ProductPlan pp12 = new ProductPlan(products.get(5), 20.0);

        MealPlan mp1 = new MealPlan();
        mp1.getProductPlans().add(pp11);
        mp1.getProductPlans().add(pp12);
//        pp11.setMealPlan(mp1);
//        pp12.setMealPlan(mp1);

        ProductPlan pp21 = new ProductPlan(products.get(0), 10.0);
        ProductPlan pp22 = new ProductPlan(products.get(1), 2.0);
        ProductPlan pp23 = new ProductPlan(products.get(5), 5.0);

        MealPlan mp2 = new MealPlan();
        mp2.getProductPlans().add(pp21);
        mp2.getProductPlans().add(pp22);
        mp2.getProductPlans().add(pp23);
//        pp21.setMealPlan(mp2);
//        pp22.setMealPlan(mp2);
//        pp23.setMealPlan(mp2);

        DayFoodPlan dfp = new DayFoodPlan();
        dfp.setDate(LocalDateTime.now().withDayOfMonth(28));
//        dfp.setUser(user);
        user.getDayFoodPlans().add(dfp);
        dfp.getMealPlans().add(mp1);
        dfp.getMealPlans().add(mp2);
//        mp1.setDayFoodPlan(dfp);
//        mp2.setDayFoodPlan(dfp);

        dayFoodPlanDAO.saveOrUpdate(dfp);

//        LOG.info("Saved DayFoodPlan: " + dfp);
    }
}
