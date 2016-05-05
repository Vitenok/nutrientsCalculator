package com.iti.foodcalc.core.service;

import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.*;
import com.iti.foodcalc.core.service.helper.ProductsSeparator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PlanningService {
    private static final Logger LOG = LogManager.getLogger(PlanningService.class);

    @Value("${calculator.protein.caloriesInGram}")
    private int caloriesInProtein;
    @Value("${calculator.fat.caloriesInGram}")
    private int caloriesInFat;
    @Value("${calculator.carbohydrate.caloriesInGram}")
    private int caloriesInCarbohydrate;


    @Autowired
    ProductsSeparator productsSeparator;

    @Autowired
    ProductWeightCalculationService productWeightCalculationService;

    @Autowired
    UserDAO userDAO;

    public List<MealPlan> createPlan(User user, List<List<Product>> products, LocalDateTime date){
        productsSeparator.init(user, products, date);

        Map<Product, Double> productsWithWeight = productWeightCalculationService.calcWeight(productsSeparator.getProductsToCalculate(), getConstraints(user));

        DayFoodPlan dayFoodPlan = createDayFoodPlan(products, date, productsWithWeight);

        saveDayFoodPlan(dayFoodPlan, user);

        return dayFoodPlan.getMealPlans();
    }

    private Product getConstraints(User user) {
        double protein = user.getTotalCalories()*user.getProteinPercent()/100/caloriesInProtein
                - productsSeparator.getRemoveFromConstraints().getProtein();
        double fat = user.getTotalCalories()*user.getFatPercent()/100/caloriesInFat
                - productsSeparator.getRemoveFromConstraints().getCarbo();
        double carbohydrate = user.getTotalCalories()*user.getCarbohydratePercent()/100/caloriesInCarbohydrate
                - productsSeparator.getRemoveFromConstraints().getFat();

        return new Product("Constraints", user.getTotalCalories()-productsSeparator.getRemoveFromConstraints().getkCal(), protein, fat, carbohydrate);
    }

    private DayFoodPlan createDayFoodPlan(List<List<Product>> products, LocalDateTime date, Map<Product, Double> productsMap) {
        DayFoodPlan dayFoodPlan = new DayFoodPlan();
        dayFoodPlan.setDate(date);
        for (List<Product> productList : products) {
            MealPlan mealPlan = new MealPlan();
            for (Product product : productList) {
                ProductPlan productPlan = null;
                Double calculatedWeight = productsMap.get(product);
                if (calculatedWeight == null) {
                    productPlan = new ProductPlan(product, productsSeparator.getProductsMap().get(product.getId()));
                } else {
                    Integer frequency = productsSeparator.getFrequencies().get(product.getId());
                    productPlan = new ProductPlan(product, calculatedWeight / frequency);
                }
                mealPlan.addProductPlan(productPlan);
            }
            dayFoodPlan.addMealPlan(mealPlan);
        }
        return dayFoodPlan;
    }

    private DayFoodPlan saveDayFoodPlan(DayFoodPlan dayFoodPlan, User user) {
        DayFoodPlan previousDayFoodPlan = user.getDayFoodPlans().stream().filter(d -> dayFoodPlan.getDate().toLocalDate().equals(d.getDate().toLocalDate())).findFirst().orElse(null);
        if (previousDayFoodPlan != null) {
            user.getDayFoodPlans().remove(previousDayFoodPlan);
        }
        user.addDayFoodPlan(dayFoodPlan);
        userDAO.saveOrUpdate(user);

        LOG.info("DayFoodPlan for user " + user + " saved successfully");

        return dayFoodPlan;
    }
}
