package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.DayFoodPlan;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Repository("dayFoodPlanDAO")
@Transactional
public class DayFoodPlanDAO extends GenericDAO<DayFoodPlan> {

    public void saveOrUpdate(DayFoodPlan plan) {
        getSession().saveOrUpdate(plan);
    }

    public DayFoodPlan findByUserIdAndDate(int userId, Date date){
        return (DayFoodPlan) createCriteria().
                add(Restrictions.eq("userId", userId)).
                add(Restrictions.eq("date", date)).
                uniqueResult();
    }

}
