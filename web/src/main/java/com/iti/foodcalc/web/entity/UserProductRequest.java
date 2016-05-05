package com.iti.foodcalc.web.entity;

import com.iti.foodcalc.core.entity.Product;

import java.util.List;

public class UserProductRequest {

    private List<Product> products;
    private int userId;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
