package com.iti.foodcalc.core.dao;

import com.iti.foodcalc.core.entity.Product;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ProductsDAO extends GenericDAO<Product> {

    public List<Product> findByUser(int userId) {
        return createCriteria()
                .add(Restrictions.eq("user.id", userId))
                .list();
    }

    public List<Product> findShared() {
        return createCriteria()
                .add(Restrictions.isNull("user"))
                .list();
    }

    public List<Product> findByIds(List<Integer> ids) {
        return createCriteria()
                .add(Restrictions.in("id", ids))
                .list();
    }

    public List<Product> findByNames(List<String> names) {
        return createCriteria()
                .add(Restrictions.in("name", names))
                .list();
    }

    public void save(List<Product> products) {
        for (Product p : products) {
            saveOrUpdate(p);
        }
    }

}
