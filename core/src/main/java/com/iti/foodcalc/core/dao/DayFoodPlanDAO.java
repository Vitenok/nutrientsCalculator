package com.iti.foodcalc.core.dao;

import com.iti.foodcalc.core.entity.DayFoodPlan;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public class DayFoodPlanDAO extends GenericDAO<DayFoodPlan> {

    public DayFoodPlan findByUserIdAndDate(int userId, LocalDateTime dateTime) {
        return (DayFoodPlan) createCriteria().
                add(Restrictions.eq("user.id", userId)).
                add(Restrictions.ge("date", dateTime.withHour(0).withMinute(0).withSecond(0).withNano(0))).
                add(Restrictions.le("date", dateTime.withHour(23).withMinute(59).withSecond(59).withNano(999))).
                uniqueResult();
    }
}
