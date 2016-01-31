package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;
import org.junit.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by pkrasnom on 28.01.2016.
 */
public class SimpleProductWeightCalculatorServiceTest {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("food_composition");
    EntityManager em = emf.createEntityManager();

    SimpleProductWeightCalculatorService service = new SimpleProductWeightCalculatorService();

    @Test
    public void testCalculateWeightOfProducts() throws Exception {

        List<Product> products = findProductsByName(new ArrayList(){{
            add("Egg white, raw");
            add("Chicken, breast, with skin, raw");
            add("Beef, minced meat, max 6 % fat, raw");
            add("Rice, brown, long-grain, uncooked");
            add("Crisp bread, wholemeal flour, rye, Husman");
            add("Cheese, hard, Cheddar");
        }});
        Product restriction = new Product("", 0, 1200*0.4/4, 1200*0.2/9, 1200*0.4/4);
        Map<Product, Double> productByWeight = service.calculateWeightOfProducts(products, restriction);
        Map<Product, Double> productByWeightByNNLS = service.calculateWeightOfProductsWithNNLS(products, restriction);

        double calories = 0;
        double caloriesNNLS = 0;
        for (Product p : productByWeight.keySet()) {
            System.out.println(p.getName() + " : " + productByWeight.get(p) + " : " + productByWeightByNNLS.get(p));
            calories=calories+(p.getProtein()*4+p.getFat()*9+p.getCarbo()*4)*(productByWeight.get(p)/100);
            caloriesNNLS=caloriesNNLS+(p.getProtein()*4+p.getFat()*9+p.getCarbo()*4)*(productByWeightByNNLS.get(p)/100);
        }
        System.out.println("Total calories: " + calories);
        System.out.println("Total caloriesNNLS: " + caloriesNNLS);
    }

    public List<Product> findProductsByName(List<String> names) {
        List<Product> products = new ArrayList<>();
        Query namedQuery = em.createNamedQuery(Product.GET__PRODUCT_BY_NAME);
        for (String name: names) {
            namedQuery.setParameter("name", name);
            products.add((Product) namedQuery.getSingleResult());
        }
        return products;
    }
}