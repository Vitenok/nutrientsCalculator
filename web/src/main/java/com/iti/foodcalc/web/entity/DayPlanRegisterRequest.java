package com.iti.foodcalc.web.entity;

import com.iti.foodcalc.core.entity.MealPlan;

import java.util.List;

public class DayPlanRegisterRequest {

    private int userId;
    private List<MealPlan> meals;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<MealPlan> getMeals() {
        return meals;
    }

    public void setMeals(List<MealPlan> meals) {
        this.meals = meals;
    }
}
