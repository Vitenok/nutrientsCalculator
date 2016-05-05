package com.iti.foodcalc.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class MealPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    @JsonBackReference("dayFoodPlan")
    private DayFoodPlan dayFoodPlan;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "mealPlan", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference("mealPlan")
    private List<ProductPlan> productPlans;

    public void addProductPlan(ProductPlan productPlan) {
        getProductPlans().add(productPlan);
        productPlan.setMealPlan(this);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public DayFoodPlan getDayFoodPlan() {
        return dayFoodPlan;
    }

    public void setDayFoodPlan(DayFoodPlan dayFoodPlan) {
        this.dayFoodPlan = dayFoodPlan;
    }

    public List<ProductPlan> getProductPlans() {
        if (productPlans == null) {
            productPlans = new ArrayList<>();
        }
        return productPlans;
    }

    public void setProductPlans(List<ProductPlan> productPlans) {
        this.productPlans = productPlans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealPlan)) return false;

        MealPlan mealPlan = (MealPlan) o;

        if (getId() != mealPlan.getId()) return false;
        if (getDayFoodPlan() != null ? !getDayFoodPlan().equals(mealPlan.getDayFoodPlan()) : mealPlan.getDayFoodPlan() != null)
            return false;
        return !(getProductPlans() != null ? !getProductPlans().equals(mealPlan.getProductPlans()) : mealPlan.getProductPlans() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getDayFoodPlan() != null ? getDayFoodPlan().hashCode() : 0);
        result = 31 * result + (getProductPlans() != null ? getProductPlans().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "MealPlan{" +
                "id=" + id +
                ", productPlans=" + productPlans +
                '}';
    }
}
