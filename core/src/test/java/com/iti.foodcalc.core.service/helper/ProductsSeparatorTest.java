package com.iti.foodcalc.core.service.helper;

import com.iti.foodcalc.core.dao.ProductsDAO;
import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.Product;
import com.iti.foodcalc.core.entity.User;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:core-application-context.xml")
public class ProductsSeparatorTest {

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProductsSeparator productsSeparator;

    @Test
    @Ignore
    public void testProductsSeparator() {
        /**
        List<Product> ps = findByNames(new ArrayList(){{
            add("Goat cheese, hard, white, Balsfjord");
            add("Oil, olive");
            add("Chicken, breast, without skin, raw");
            add("Sweet potato, raw");
            add("Broccoli, with stalk, Norwegian, raw");
            add("User product3");
            add("Common Product with serving2");
        }});
        ps.stream().forEach(System.out::println);
         **/

        /*
        Product{kCal=398.0, id=77, name='Goat cheese, hard, white, Balsfjord', protein=23.0, carbo=0.0, fat=34.0, type=PRODUCT}
        Product{kCal=104.0, id=175, name='Chicken, breast, without skin, raw', protein=23.8, carbo=0.0, fat=1.0, type=PRODUCT}
        Product{kCal=28.0, id=952, name='Broccoli, with stalk, Norwegian, raw', protein=3.2, carbo=1.9, fat=0.3, type=PRODUCT}
        Product{kCal=80.0, id=1008, name='Sweet potato, raw', protein=1.6, carbo=16.8, fat=0.0, type=PRODUCT}
        Product{kCal=892.0, id=1286, name='Oil, olive', protein=0.2, carbo=0.0, fat=99.0, type=PRODUCT}
        Product{kCal=16.0, id=1989, name='Common Product with serving2', protein=51.0, carbo=89.0, fat=75.0, type=SUPPLEMENT}
        Product{kCal=16.0, id=1990, name='User product3', protein=51.0, carbo=89.0, fat=75.0, type=SUPPLEMENT}
         */


        List<List<Product>> products =
                new ArrayList<List<Product>>() {{
                    add(new ArrayList<Product>(){{
                        add(new Product("Goat cheese, hard, white, Balsfjord", 77, 398, 23, 34, 0));
                        add(new Product("Oil, olive", 1286, 892, 0.1, 99, 0.0));
                        add(new Product("Chicken, breast, without skin, raw", 175, 104.0, 23.8, 1.0, 0.0));
                        add(new Product("Sweet potato, raw", 1008, 80, 1.6, 0, 16.8));
                        add(new Product("Banana, raw", 1112, 892, 0.1, 99, 0.0));
                    }});
                    add(new ArrayList<Product>(){{
                        add(new Product("Common Product with serving2", 1989, 392, 12, 6, 68));
                        get(0).setType(Product.TYPE.SUPPLEMENT);
                        get(0).setServing(15);
                        add(new Product("Broccoli, with stalk, Norwegian, raw", 952, 28.0, 3.2, 0.3, 1.9));
                    }});
                    add(new ArrayList<Product>(){{
                        add(new Product("User product3", 1990, 80, 1.6, 0, 16.8));
                        get(0).setType(Product.TYPE.SUPPLEMENT);
                        get(0).setServing(15);
                        add(new Product("Sweet potato, raw", 1008, 80, 1.6, 0, 16.8));
                    }});
                    add(new ArrayList<Product>(){{
                        add(new Product("Chicken, breast, without skin, raw", 175, 104.0, 23.8, 1.0, 0.0));
                        add(new Product("Sweet potato, raw", 1008, 80, 1.6, 0, 16.8));
                        add(new Product("Banana, raw", 1112, 85, 0.1, 99, 0.0));
                    }});
                }};

        User u = userDAO.findById(1906);

        productsSeparator.init(u, products, LocalDateTime.now());
    }

    private List<Product> findByNames(List<String> names) {
        return productsDAO.findByNames(names);
    }
}
