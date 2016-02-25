package com.iti.foodCalculator.service;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.Category;
import com.iti.foodCalculator.entity.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= "classpath:core-application-context.xml")
public class ProductWeightCalculationServiceTest {

    @Autowired
    ProductWeightCalculationService service;

    @Autowired
    ProductsDAO dao;

    @Test
    public void testCalculateWeightOfProducts() throws Exception {
        List<Product> products = dao.findByNames(new ArrayList() {{
            add("Egg white, raw");
            add("Chicken, breast, with skin, raw");
            add("Beef, minced meat, max 6 % fat, raw");
            add("Rice, brown, long-grain, uncooked");
            add("Crisp bread, wholemeal flour, rye, Husman");
            add("Cheese, hard, Cheddar");
        }});
//        Product restriction = new Product("", 1200, 1200*0.4/4, 1200*0.2/9, 1200*0.4/4);
        Product restriction = new Product("", 600, 600*0.4/4, 600*0.2/9, 600*0.4/4);
        restriction.setCategory(new Category("testName", null));

        Map<Product, Double> productByWeight = service.calculateWeightOfProducts(products, restriction);

    }
}