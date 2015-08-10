package com.iti.foodCalculator.entity;

import java.io.Serializable;
import java.util.List;

// Java class representing JSON from client
public class CalculationInputDomainModel implements Serializable {

    private List<Product> products;
    private List<SupplementItem> supplementItems;
    private DailyMacroelementsInput dailyMacroelementsInput;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<SupplementItem> getSupplementItems() {
        return supplementItems;
    }

    public void setSupplementItems(List<SupplementItem> supplementItems) {
        this.supplementItems = supplementItems;
    }

    public DailyMacroelementsInput getDailyMacroelementsInput() {
        return dailyMacroelementsInput;
    }

    public void setDailyMacroelementsInput(DailyMacroelementsInput dailyMacroelementsInput) {
        this.dailyMacroelementsInput = dailyMacroelementsInput;
    }
}
