package com.iti.foodCalculator.service;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.Category;
import com.iti.foodCalculator.entity.Product;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<Product> products =
                new ArrayList<Product>(){{
                    int i = 1;
            add(new Product("Goat cheese, hard, white, Balsfjord", i++, 398, 23, 34, 0));
            add(new Product("Oat flakes, Havreflakes", i++, 392, 12, 6, 68));
            add(new Product("Broccoli, with stalk, Norwegian, raw", i++, 28.0, 3.2, 0.3, 1.9));
            add(new Product("Oil, olive", i++, 892, 0.1, 99, 0.0));
            add(new Product("Chicken, breast, without skin, raw", i++, 104.0, 23.8, 1.0, 0.0));
            add(new Product("Rice, brown, long-grain, uncooked", i++, 364.0, 9.4, 3.0, 72.8));
            add(new Product("Salmon, ocean, raw", i++, 182.0, 19.7, 11.5, 0.0));
            add(new Product("Cod, wild, raw", i++, 81, 17.9, 1.1, 0.0));
            add(new Product("Sweet potato, raw", i++, 80, 1.6, 0, 16.8));

                }};
//                dao.findByNames(new ArrayList() {{
//            add("Egg white, raw");
//            add("Chicken, breast, with skin, raw");
//            add("Beef, minced meat, max 6 % fat, raw");
//            add("Rice, brown, long-grain, uncooked");
//            add("Crisp bread, wholemeal flour, rye, Husman");
//            add("Cheese, hard, Cheddar");
//        }});
        Product restriction = new Product("", 1200, 1200*0.4/4, 1200*0.2/9, 1200*0.4/4);
//        Product restriction = new Product("", 1360.15, 1360.15*0.4/4, 1360.15*0.2/9, 1360.15*0.4/4);
        restriction.setCategory(new Category("testName", null));

        Map<Product, Double> productByWeight = service.calcWeight(products, restriction);

    }

    @Test
    public void test() {
        for (int i = 0; i < Math.pow(2, 5); i++) {
            Arrays.asList(Integer.toBinaryString(i).split("")).stream().mapToInt(s -> Integer.valueOf(s)).forEach(System.out::print);
            System.out.println();
        }
    }
}