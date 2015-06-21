package com.iti.foodCalculator.entity;

public class SupplementItem {
    private String id;
    private String name;
    private double kcal;
    private double protein;
    private double fat;
    private double carb;


    public SupplementItem(String id, String name, double kcal, double protein, double fat, double carb) {
        this.id = id;
        this.name = name;
        this.kcal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carb = carb;
    }

    public String getId() {
        return id;
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
}
