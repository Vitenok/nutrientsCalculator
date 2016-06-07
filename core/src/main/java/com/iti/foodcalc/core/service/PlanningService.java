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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public DayFoodPlan createPlan(User user, List<UserProductServing> servings, List<List<Product>> products, LocalDateTime date){
        productsSeparator.init(user, products, date);

        Map<Product, Integer> productsWithWeight = mapDoubleToInt(productWeightCalculationService.calcWeight(productsSeparator.getProductsToCalculate(), getConstraints(user)));

        DayFoodPlan dayFoodPlan = createDayFoodPlan(products, productsSeparator.getDate(), productsWithWeight, user);

        saveDayFoodPlan(dayFoodPlan, user);

        saveUserProductServings(servings, user);

        return dayFoodPlan;
    }

    private Map<Product, Integer> mapDoubleToInt(Map<Product, Double> productDoubleMap) {
        Map<Product, Integer> m = new HashMap<Product, Integer>(){{
            productDoubleMap.forEach((k,v)->put(k,(int)Math.round(v)));
        }};
        return m;
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

    private DayFoodPlan createDayFoodPlan(List<List<Product>> products, LocalDateTime date, Map<Product, Integer> productsMap, User user) {
        DayFoodPlan dayFoodPlan = new DayFoodPlan();
        dayFoodPlan.setDate(date);
        dayFoodPlan.setUser(user);
        for (List<Product> productList : products) {
            MealPlan mealPlan = new MealPlan();
            for (Product product : productList) {
                ProductPlan productPlan;
                Integer calculatedWeight = productsMap.get(product);
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

    private void saveUserProductServings(List<UserProductServing>  servings, User user) {
        for(UserProductServing s : servings) {
            // Trying to update user products first
            Optional<Product> p = user.getProducts().stream().filter(up -> up.getId() == s.getProductId()).findFirst();
            if (p.isPresent()) {
                p.get().setServing(s.getWeight());
                continue;
            }
            // Otherwise - updating user servings
            Optional<UserProductServing> ups = user.getServings().stream().filter(us -> us.getId() == s.getId()).findFirst();
            if (ups.isPresent()) {
                ups.get().setWeight(s.getWeight());
            } else {
                user.addServing(s);
            }
        }
        userDAO.saveOrUpdate(user);
    }
}
