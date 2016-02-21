package com.iti.nutrientsCalculator;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public void login(@RequestParam String name, @RequestParam String socialNetwork, @RequestParam String token) {
        System.out.println(name+"logged in from " + socialNetwork + " with user_id '" + token + "'");
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout() {
        System.out.println("logging out");
    }
}
