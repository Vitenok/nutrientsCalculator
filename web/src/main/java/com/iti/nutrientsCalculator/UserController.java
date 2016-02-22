package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.dao.UserDAO;
import com.iti.foodCalculator.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    UserDAO usersDAO;

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public @ResponseBody User user(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(@RequestParam String name, @RequestParam String socialNetwork, @RequestParam String token, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = usersDAO.find(name, socialNetwork, token);
            if (user == null) {
                user = new User(name, socialNetwork, token);
                usersDAO.save(user);
            }
            session.setAttribute("user", user);
        }
        System.out.println(name+"logged in from " + socialNetwork + " with user_id '" + token + "'");
        return "";
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        System.out.println("logging out");
        return "";
    }
}
