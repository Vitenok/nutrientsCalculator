package com.iti.entity;

import com.iti.foodCalculator.entity.Product;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DayFoodPlanRequest {

    private LocalDateTime date;
    private Product constrains;
    private List<List<Product>> productsLists;

    //Calculated fields
    private List<Product> flatProducts;
    private List<Product> uniqueProducts;

    public List<Product> getFlatProducts() {
        if (flatProducts == null) {
            flatProducts = productsLists.stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        return flatProducts;
    }

    public List<Product> getUniqueProducts() {
        if (uniqueProducts == null) {
            uniqueProducts = productsLists.stream().flatMap(Collection::stream).distinct().collect(Collectors.toList());
        }
        return uniqueProducts;
    }

    public int frequency(Product product) {
        return Collections.frequency(getFlatProducts(), product);
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public Product getConstrains() {
        return constrains;
    }

    public void setConstrains(Product constrains) {
        this.constrains = constrains;
    }

    public List<List<Product>> getProductsLists() {
        return productsLists;
    }

    public void setProductsLists(List<List<Product>> productsLists) {
        this.productsLists = productsLists;
    }
}
