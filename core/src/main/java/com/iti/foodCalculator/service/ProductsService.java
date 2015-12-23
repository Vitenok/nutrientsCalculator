package com.iti.foodCalculator.service;


import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.Product;

import java.util.List;

public class ProductsService {
    private ProductsDAO productsDAO = new ProductsDAO();

    public List<Product> getAllProducts(){
        productsDAO.beginTransaction();
        List<Product> productList = productsDAO.findAll();
        productsDAO.closeTransaction();
        return productList;
    }
}
