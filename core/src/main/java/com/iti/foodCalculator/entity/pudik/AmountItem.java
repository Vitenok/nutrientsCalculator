package com.iti.foodCalculator.entity.pudik;

// Represents amount of food after calculation
public class AmountItem {
    private String name;
    private double amount;
    private double totalProtein;
    private double totalFat;
    private double totalCarb;
    private double totalCalories;

    public AmountItem(String name, double amount, double prot, double fat, double carb, double cal) {
        this.name = name;
        this.amount = amount;
        this.totalProtein = prot;
        this.totalFat = fat;
        this.totalCarb = carb;
        this.totalCalories = cal;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(double totalCalories) {
        this.totalCalories = totalCalories;
    }

    public double getTotalCarb() {
        return totalCarb;
    }

    public void setTotalCarb(double totalCarb) {
        this.totalCarb = totalCarb;
    }

    public double getTotalFat() {
        return totalFat;
    }

    public void setTotalFat(double totalFat) {
        this.totalFat = totalFat;
    }

    public double getTotalProtein() {
        return totalProtein;
    }

    public void setTotalProtein(double totalProtein) {
        this.totalProtein = totalProtein;
    }


    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
