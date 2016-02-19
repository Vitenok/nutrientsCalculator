package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.Product;
import org.springframework.stereotype.Repository;

@Repository("categoryDAO")
public class CategoryDAO extends GenericDAO<Product> {
}
