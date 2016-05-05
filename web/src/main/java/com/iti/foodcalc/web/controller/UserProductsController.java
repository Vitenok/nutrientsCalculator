package com.iti.foodcalc.web.controller;

import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.Product;
import com.iti.foodcalc.core.entity.User;
import com.iti.foodcalc.web.entity.UserProductRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class UserProductsController {
    @Autowired
    UserDAO userDAO;

    @Autowired
    SimpleCacheManager simpleCacheManager;

    @RequestMapping(value = "/user/products/save", method = RequestMethod.POST)
    public
    @ResponseBody
    List<Product> save(@RequestBody UserProductRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userDAO.findById(request.getUserId());
            session.setAttribute("user", user);
        }

        user.getProducts().clear();
        user.addProducts(request.getProducts());
        userDAO.saveOrUpdate(user);

        return user.getProducts();
    }
}
