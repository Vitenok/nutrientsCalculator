package com.iti.foodcalc.core.service;

import com.iti.foodcalc.core.dao.ProductsDAO;
import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.Category;
import com.iti.foodcalc.core.entity.Product;
import com.iti.foodcalc.core.entity.User;
import com.iti.foodcalc.core.entity.UserProductServing;
import com.iti.foodcalc.core.service.helper.ProductsSeparator;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:core-application-context.xml")
public class ProductWeightCalculationServiceTest {

    @Autowired
    ProductWeightCalculationService service;

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    ProductsSeparator productsSeparator;

    @Autowired
    PlanningService planningService;

    @Test
    public void dummyTestForUpdatingDB(){
    }

    @Ignore
    @Test
    public void testCalculateWeightOfProducts() throws Exception {
        List<Product> products =
                new ArrayList<Product>() {{
                    int i = 1;
                    add(new Product("Goat cheese, hard, white, Balsfjord", ++i, 398, 23, 34, 0));
                    add(new Product("Oat flakes, Havreflakes", ++i, 392, 12, 6, 68));
                    add(new Product("Broccoli, with stalk, Norwegian, raw", ++i, 28.0, 3.2, 0.3, 1.9));
//                    add(new Product("Oil, olive", ++i, 892, 0.1, 99, 0.0));
                    add(new Product("Chicken, breast, without skin, raw", ++i, 104.0, 23.8, 1.0, 0.0));
                    add(new Product("Rice, brown, long-grain, uncooked", ++i, 364.0, 9.4, 3.0, 72.8));
                    add(new Product("Salmon, ocean, raw", ++i, 182.0, 19.7, 11.5, 0.0));
//                    add(new Product("Cod, wild, raw", ++i, 81, 17.9, 1.1, 0.0));
//                    add(new Product("Sweet potato, raw", ++i, 80, 1.6, 0, 16.8));

                }};

        Product restriction = new Product("", 1360.15, 1360.15 * 0.4 / 4, 1360.15 * 0.2 / 9, 1360.15 * 0.4 / 4);
        restriction.setCategory(new Category("testName", null));

        Map<Product, Double> productByWeight = service.calcWeight(products, restriction);

        for (double weight : productByWeight.values()) {
            assertTrue("All product should be taken", weight > 0);
        }
    }

    @Ignore
    @Test
    public void playWithUser() {
        Product p1 = product("New Product Type PRODUCT");
        productsDAO.saveOrUpdate(p1);
        Product p2 = product("New Product Type SUPPLEMENT");
        p2.setType(Product.TYPE.SUPPLEMENT);
        productsDAO.saveOrUpdate(p2);

//        User u = userDAO.findById(1906);
//        Random r = new Random(100);
//        for (int i = 0; i < 10; i++) {
//            Product p = new Product("My Custom Product " + i, r.nextInt(100) + 1, r.nextInt(100) + 1, r.nextInt(100) + 1, r.nextInt(100) + 1);
//            u.addProduct(p);
//        }
//
//        userDAO.saveOrUpdate(u);
//        userDAO.delete(u);
    }

    @Ignore
    @Test
    public void playWithUserServing() {

        Product productWithServing = product("Common Product with serving"+1);
        productWithServing.setServing(50);
        productsDAO.saveOrUpdate(productWithServing);
        productWithServing = product("Common Product with serving"+2);
        productWithServing.setServing(60);
        productsDAO.saveOrUpdate(productWithServing);


        User u = userDAO.findById(1906);

        Product userProduct = product("User product" + 3);
        userProduct.setServing(100);
        u.addProduct(userProduct);
        userDAO.saveOrUpdate(u);

        UserProductServing userProductServing = new UserProductServing();
        userProductServing.setProductId(productWithServing.getId());
        userProductServing.setWeight(70);
        u.addServing(userProductServing);
        userDAO.saveOrUpdate(u);

//        u.getServings().add(new UserProductServing());
    }

    private Product product(String name) {
        Random r = new Random(100);
        return new Product(name, r.nextInt(100) + 1, r.nextInt(100) + 1, r.nextInt(100) + 1, r.nextInt(100) + 1);
    }

    @Ignore
    @Test
    public void testSharedProductNotSavedWithProductPlan() {
        Product sharedProduct = productsDAO.findById(1989);
        sharedProduct.setServing(1000);

        UserProductServing serving = new UserProductServing();
        serving.setWeight(sharedProduct.getServing());
        serving.setProductId(sharedProduct.getId());
        User u = userDAO.findById(1906);
        u.addServing(serving);

        userDAO.saveOrUpdate(u);
    }

}