package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.Product;

import java.util.List;

public class ProductsDAO extends GenericDAO<Product> {
    public ProductsDAO() {
        super(Product.class);
    }

//    public List<Product> getAllFoodItems() {
//        return super.findResultList(Product.GET_ALL_PRODUCTS, null);
//    }

}
