package com.iti.foodcalc.web.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iti.foodcalc.core.entity.Product;

import java.time.LocalDateTime;
import java.util.List;

public class DayFoodPlanRequest {

    @JsonFormat(pattern="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime date;
    private int userId;
    private List<List<Product>> productsLists;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public List<List<Product>> getProductsLists() {
        return productsLists;
    }

    public void setProductsLists(List<List<Product>> productsLists) {
        this.productsLists = productsLists;
    }
}
