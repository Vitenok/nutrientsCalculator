package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.DayFoodPlan;
import com.iti.foodCalculator.entity.User;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository("dayFoodPlanDAO")
public class DayFoodPlanDAO extends GenericDAO<DayFoodPlan> {

    public DayFoodPlan findByUserIdAndDate(int userId, Date date){
        return (DayFoodPlan) createCriteria().
                add(Restrictions.eq("userId", userId)).
                add(Restrictions.eq("date", date)).
                uniqueResult();
    }

//    public void saveOrUpdate(DayFoodPlan dayFoodPlan) {
//        User user = dayFoodPlan.getUser();
//        if (user != null && !user.getDayFoodPlans().isEmpty()) {
//
//        }
//    }

}
