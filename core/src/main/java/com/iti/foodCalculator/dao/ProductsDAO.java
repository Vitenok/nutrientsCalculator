package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.Product;

import java.util.List;

public class ProductsDAO extends GenericDAO<Product> {
    public ProductsDAO() {
        super(Product.class);
    }

    @Override
    public List<Product> findAll() {
        return super.findResultList(Product.GET_ALL_PRODUCTS, null);
    }

}
