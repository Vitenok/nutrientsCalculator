package com.iti.foodCalculator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class DayFoodPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private LocalDateTime date;

    @ManyToOne//(cascade = CascadeType.ALL)
//    @JoinColumn(name = "user_id")
    @JsonBackReference("user")
    private User user;

    @OneToMany(cascade = CascadeType.ALL/*, mappedBy = "dayFoodPlan"*/)
    @LazyCollection(LazyCollectionOption.FALSE)
//    @JoinColumn(name = "day_food_plan_id")
    @OrderBy("id")
    @JsonManagedReference("dayFoodPlan")
    private List<MealPlan> mealPlans;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<MealPlan> getMealPlans() {
        if(mealPlans==null){
            mealPlans=new ArrayList<>();
        }
        return mealPlans;
    }

    public void setMealPlans(List<MealPlan> mealPlans) {
        this.mealPlans = mealPlans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DayFoodPlan)) return false;

        DayFoodPlan that = (DayFoodPlan) o;

        if (getId() != that.getId()) return false;
        if (getDate() != null ? !getDate().equals(that.getDate()) : that.getDate() != null) return false;
        if (getUser() != null ? !getUser().equals(that.getUser()) : that.getUser() != null) return false;
        return !(getMealPlans() != null ? !getMealPlans().equals(that.getMealPlans()) : that.getMealPlans() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getDate() != null ? getDate().hashCode() : 0);
        result = 31 * result + (getUser() != null ? getUser().hashCode() : 0);
        result = 31 * result + (getMealPlans() != null ? getMealPlans().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DayFoodPlan{" +
                "id=" + id +
                ", date=" + date +
                ", user=" + user +
                ", mealPlans=" + mealPlans +
                '}';
    }
}
