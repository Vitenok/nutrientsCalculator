package com.iti.foodcalc.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;

@Entity
public class ProductPlan {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @OneToOne
    private Product product;

    @ManyToOne
    @JsonBackReference("mealPlan")
    private MealPlan mealPlan;

    private int weight;

    private int registeredWeight;

    public ProductPlan() {
    }

    public ProductPlan(Product product, int weight) {
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

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getRegisteredWeight() {
        return registeredWeight;
    }

    public void setRegisteredWeight(int registeredWeight) {
        this.registeredWeight = registeredWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductPlan)) return false;

        ProductPlan that = (ProductPlan) o;

        if (id != that.id) return false;
        if (weight != that.weight) return false;
        if (registeredWeight != that.registeredWeight) return false;
        return !(product != null ? !product.equals(that.product) : that.product != null);

    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (product != null ? product.hashCode() : 0);
        result = 31 * result + weight;
        result = 31 * result + registeredWeight;
        return result;
    }

    @Override
    public String toString() {
        return "ProductPlan{" +
                "id=" + id +
                ", product=" + product.getName() +
                ", weight=" + weight +
                ", registeredWeight=" + registeredWeight +
                '}';
    }
}
