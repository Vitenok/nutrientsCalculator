package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.dao.UserDAO;
import com.iti.foodCalculator.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam String name, @RequestParam String socialNetwork, @RequestParam String token, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = usersDAO.find(name, socialNetwork, token);
            if (user == null) {
                user = new User(name, socialNetwork, token);
                usersDAO.saveOrUpdate(user);
                LOG.debug(name + " signed up from " + socialNetwork + " with user_id '" + token + "'");
            }
            session.setAttribute("user", user);
        }
        LOG.debug(name + " logged in from " + socialNetwork + " with user_id '" + token + "'");
        return "";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        LOG.debug("logging out " + user.getName());
        session.removeAttribute("user");
        return "";
    }
}
