package com.iti.foodCalculator.service;

import com.iti.foodCalculator.dao.DayFoodPlanDAO;
import com.iti.foodCalculator.entity.DayFoodPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("dayFoodPlanService")
public class DayFoodPlanService {

    @Autowired
    DayFoodPlanDAO dayFoodPlanDAO;

    public void saveOrUpdate(DayFoodPlan dayFoodPlan) {
        if (dayFoodPlan.getId() != 0) {

        }
    }
}
