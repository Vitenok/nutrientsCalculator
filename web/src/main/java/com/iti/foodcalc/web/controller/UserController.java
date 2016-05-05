package com.iti.foodcalc.web.controller;

import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.User;
import com.iti.foodcalc.web.entity.UserLoginRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {
    private static final Logger LOG = LogManager.getLogger(UserController.class);

    @Autowired
    UserDAO usersDAO;

    @RequestMapping(value = "/user/login", method = RequestMethod.POST)
    public @ResponseBody User login(@RequestBody UserLoginRequest userWrapper, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = usersDAO.find(userWrapper.getName(), userWrapper.getSocialNetwork(), userWrapper.getSocialNetworkId());
            if (user == null) {
                user = new User(userWrapper.getName(), userWrapper.getSocialNetwork(), userWrapper.getSocialNetworkId());
                usersDAO.saveOrUpdate(user);
                LOG.debug(userWrapper.getName() + " signed up from " + userWrapper.getSocialNetwork() + " with user_id '" + userWrapper.getSocialNetworkId() + "'");
            }
            session.setAttribute("user", user);
        }
        LOG.debug(userWrapper.getName() + " logged in from " + userWrapper.getSocialNetwork() + " with user_id '" + userWrapper.getSocialNetworkId() + "'");

        return user;
    }

    @RequestMapping(value = "/user/logout", method = RequestMethod.POST)
    public void logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            LOG.debug("logging out " + user.getName());
            session.invalidate();
        }
    }

    @RequestMapping(value = "/user/update", method = RequestMethod.POST)
    public @ResponseBody User savePersonalData(@RequestBody User user, HttpSession session) {
        usersDAO.saveOrUpdate(user);
        session.setAttribute("user", user);
        return user;
    }

}
