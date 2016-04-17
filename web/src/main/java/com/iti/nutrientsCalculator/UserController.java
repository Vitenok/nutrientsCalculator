package com.iti.nutrientsCalculator;

import com.iti.entity.UserWrapper;
import com.iti.foodCalculator.dao.UserDAO;
import com.iti.foodCalculator.entity.User;
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

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public
    @ResponseBody
    User user(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public @ResponseBody User login(@RequestBody UserWrapper userWrapper, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = usersDAO.find(userWrapper.getName(), userWrapper.getSocialNetwork(), userWrapper.getToken());
            if (user == null) {
                user = new User(userWrapper.getName(), userWrapper.getSocialNetwork(), userWrapper.getToken());
                usersDAO.saveOrUpdate(user);
                LOG.debug(userWrapper.getName() + " signed up from " + userWrapper.getSocialNetwork() + " with user_id '" + userWrapper.getToken() + "'");
            }
            session.setAttribute("user", user);
        }
        LOG.debug(userWrapper.getName() + " logged in from " + userWrapper.getSocialNetwork() + " with user_id '" + userWrapper.getToken() + "'");

        return user;
    }

    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    public void logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        LOG.debug("logging out " + user.getName());
        session.removeAttribute("user");
    }

    @RequestMapping(value = "/savePersonalData", method = RequestMethod.POST)
    public @ResponseBody User savePersonalData(@RequestBody User user, HttpSession session) {
        usersDAO.saveOrUpdate(user);
        session.setAttribute("user", user);
        return user;
    }

}
