package com.iti.foodCalculator.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class ProductPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne(cascade = CascadeType.ALL)
    private Product product;

    @ManyToOne//(cascade = CascadeType.ALL)
//    @JoinColumn(name = "meal_plan_id", insertable=false, updatable=false)
    @JsonBackReference("mealPlan")
    private MealPlan mealPlan;

    private double weight;

    public ProductPlan() {
    }

    public ProductPlan(Product product, double weight) {
        this.weight = weight;
        this.product = product;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public MealPlan getMealPlan() {
        return mealPlan;
    }

    public void setMealPlan(MealPlan mealPlan) {
        this.mealPlan = mealPlan;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPlan)) return false;

        ProductPlan that = (ProductPlan) o;

        if (getId() != that.getId()) return false;
        if (Double.compare(that.getWeight(), getWeight()) != 0) return false;
        if (getProduct() != null ? !getProduct().equals(that.getProduct()) : that.getProduct() != null) return false;
        return !(getMealPlan() != null ? !getMealPlan().equals(that.getMealPlan()) : that.getMealPlan() != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId();
        result = 31 * result + (getProduct() != null ? getProduct().hashCode() : 0);
        result = 31 * result + (getMealPlan() != null ? getMealPlan().hashCode() : 0);
        temp = Double.doubleToLongBits(getWeight());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "ProductPlan{" +
                "id=" + id +
                ", product=" + product +
                ", mealPlan=" + mealPlan +
                ", weight=" + weight +
                '}';
    }
}
