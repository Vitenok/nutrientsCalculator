package com.iti.foodCalculator.entity;

public class DailyMacroelementsInput {
    private double kcal;
    private double protein;
    private double fat;
    private double carb;


    public DailyMacroelementsInput(double kcal, double protein, double fat, double carb) {
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
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

    public void setKcal(double kcal) {
        this.kcal = kcal;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public void setCarb(double carb) {
        this.carb = carb;
    }
}
