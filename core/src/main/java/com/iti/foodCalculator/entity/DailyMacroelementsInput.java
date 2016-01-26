package com.iti.foodCalculator.entity;

import java.io.Serializable;

public class DailyMacroelementsInput implements Serializable {
    private double kcal;
    private double protein;
    private double fat;
    private double carb;

    // Required default constructor
    public DailyMacroelementsInput() {
    }

    public DailyMacroelementsInput(double kcal, double protein, double fat, double carb) {
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
    }

    public double getKcal() {
        return kcal;
    }

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getCarb() {
        return carb;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }
}
