package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.DayFoodPlan;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository("dayFoodPlanDAO")
public class DayFoodPlanDAO extends GenericDAO<DayFoodPlan> {

    public DayFoodPlan findByUserIdAndDate(int userId, LocalDateTime dateTime) {
        return (DayFoodPlan) createCriteria().
                add(Restrictions.eq("user.id", userId)).
                add(Restrictions.gt("date", dateTime.withHour(0).withMinute(0))).
                add(Restrictions.lt("date", dateTime.withHour(23).withMinute(59))).
                uniqueResult();
    }
}
