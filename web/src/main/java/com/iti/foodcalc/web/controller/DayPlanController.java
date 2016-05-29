package com.iti.foodcalc.web.controller;

import com.iti.foodcalc.core.dao.DayFoodPlanDAO;
import com.iti.foodcalc.core.dao.MealPlanDAO;
import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.DayFoodPlan;
import com.iti.foodcalc.core.entity.MealPlan;
import com.iti.foodcalc.core.entity.ProductPlan;
import com.iti.foodcalc.core.entity.User;
import com.iti.foodcalc.core.service.PlanningService;
import com.iti.foodcalc.web.entity.DayPlanRegisterRequest;
import com.iti.foodcalc.web.entity.DayPlanRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.stream.Collectors;


@Controller
public class DayPlanController {
    private static final Logger LOG = LogManager.getLogger(DayPlanController.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    DayFoodPlanDAO dayFoodPlanDAO;

    @Autowired
    MealPlanDAO mealPlanDAO;

    @Autowired
    PlanningService planningService;

    @RequestMapping(value = "/dayPlan/get", method = RequestMethod.POST)
    public
    @ResponseBody
    List<MealPlan> getFoodPlan(@RequestBody DayPlanRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userDAO.findById(request.getUserId());
            session.setAttribute("user", user);
        }
        DayFoodPlan dayFoodPlan = dayFoodPlanDAO.findByUserIdAndDate(user.getId(), request.getDate());
        return dayFoodPlan == null ? null : dayFoodPlan.getMealPlans();
    }

    @RequestMapping(value = "/dayPlan/register", method = RequestMethod.POST)
    public
    @ResponseBody
    List<MealPlan> registerFoodPlan(@RequestBody DayPlanRegisterRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userDAO.findById(request.getUserId());
            session.setAttribute("user", user);
        }

        List<Integer> ids = request.getMeals()
                .stream()
                .mapToInt(MealPlan::getId)
                .boxed()
                .collect(Collectors.toList());

        List<MealPlan> fromDB = mealPlanDAO.findByIds(ids);

        for (MealPlan mNew : request.getMeals()) {
            MealPlan mOld = fromDB.stream().filter(m-> m.getId() == mNew.getId()).findFirst().get();
            for (ProductPlan pNew : mNew.getProductPlans()) {
                ProductPlan pOld = mOld.getProductPlans().stream().filter(p -> p.getId() == pNew.getId()).findFirst().get();
                pOld.setRegisteredWeight(pNew.getRegisteredWeight());
            }
        }

        mealPlanDAO.saveOrUpdateAll(fromDB);
        return fromDB;
    }

    @RequestMapping(value = "/dayPlan/create", method = RequestMethod.POST)
    public
    @ResponseBody
    DayFoodPlan calculate(@RequestBody DayPlanRequest request, HttpSession session) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            user = userDAO.findById(request.getUserId());
            session.setAttribute("user", user);
        }

        return planningService.createPlan(user, request.getServings(), request.getProductsLists(), request.getDate());
    }
}
