package com.iti.foodCalculator.entity.pudik;

import java.io.Serializable;

public class SupplementItem implements Serializable {
    private String name;
    private double kcal;
    private double protein;
    private double fat;
    private double carb;
    private double weight;

    public SupplementItem() {
    }


    public SupplementItem(String name, double kcal, double protein, double fat, double carb, double weight) {
        this.name = name;
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
        this.weight = weight;
    }

    public String getName() {
        return name;
    }

    public double getKcal() {
        return kcal;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getCarb() {
        return carb;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
