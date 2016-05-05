package com.iti.foodcalc.web.controller;

import com.iti.foodcalc.core.dao.ProductsDAO;
import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.Product;
import com.iti.foodcalc.core.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class MainController {
    private static final Logger LOG = LogManager.getLogger(MainController.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    SimpleCacheManager simpleCacheManager;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome() {
        return "index";
    }


    @RequestMapping(value = "/main/products", method = RequestMethod.POST, produces = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    List<Product> populateProducts(@RequestBody int userId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userDAO.findById(userId);
            session.setAttribute("user", user);
        }
        List<Product> all = getSharedProducts();
        List<Product> byUser = productsDAO.findByUser(userId);
        byUser.addAll(all);
        return byUser;
    }

    public List<Product> getSharedProducts(){
        Cache cache = simpleCacheManager.getCache("products");
        if (cache.get("shared") == null) {
            cache.put("shared", productsDAO.findShared());
        }
        return (List<Product>) cache.get("shared").get();
    }
}
