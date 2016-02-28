package com.iti.foodCalculator.dao;

import com.iti.foodCalculator.entity.Product;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("productsDAO")
public class ProductsDAO extends GenericDAO<Product> {

    public List<Product> findAll() {
        return createCriteria().list();
    }

    public List<Product> findByNames(List<String> names) {
        return createCriteria().add(Restrictions.disjunction()).add(Restrictions.in("name", names)).list();
    }

    public void save(List<Product> products) {
        for (Product p : products) {
            saveOrUpdate(p);
        }
    }

}
