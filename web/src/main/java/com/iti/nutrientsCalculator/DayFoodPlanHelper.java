package com.iti.nutrientsCalculator;

import com.iti.entity.DayFoodPlanRequest;
import com.iti.foodCalculator.entity.DayFoodPlan;
import com.iti.foodCalculator.entity.MealPlan;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.entity.ProductPlan;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DayFoodPlanHelper {
    public static DayFoodPlan hydrate(DayFoodPlanRequest dayPlan, Map<Product, Double> productsMap) {
        DayFoodPlan dayFoodPlan = new DayFoodPlan();
        dayFoodPlan.setDate(dayPlan.getDate());
        for (int i = 0; i < dayPlan.getProductsLists().size(); i++) {
            MealPlan mealPlan = new MealPlan();
            for (Product product : dayPlan.getProductsLists().get(i)) {
                mealPlan.addProductPlan(new ProductPlan(product, productsMap.get(product) / dayPlan.frequency(product)));
            }
            dayFoodPlan.addMealPlan(mealPlan);
        }
        return dayFoodPlan;
    }

    public static List<List<Pair<String, Double>>> dehydrate(DayFoodPlan dayFoodPlan) {
        List<List<Pair<String, Double>>> result = new ArrayList<>();
        for (MealPlan mp : dayFoodPlan.getMealPlans()) {
            result.add(mp.getProductPlans().stream().map(pp -> new Pair<>(pp.getProduct().getName(), pp.getWeight())).collect(Collectors.toList()));
        }
        return result;
    }
}
