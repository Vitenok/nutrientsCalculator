package com.iti.foodCalculator.entity;

// Represents amount of food after calculation
public class AmountItem {
    private String name;
    private double amount;

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }

    public AmountItem(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }
}
