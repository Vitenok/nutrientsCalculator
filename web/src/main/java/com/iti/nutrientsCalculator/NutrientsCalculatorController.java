package com.iti.nutrientsCalculator;

import com.iti.entity.DayFoodPlanRequest;
import com.iti.foodCalculator.dao.DayFoodPlanDAO;
import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.dao.UserDAO;
import com.iti.foodCalculator.entity.DayFoodPlan;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.entity.User;
import com.iti.foodCalculator.service.ProductWeightCalculationService;
import javafx.util.Pair;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Controller
public class NutrientsCalculatorController {
    private static final Logger LOG = LogManager.getLogger(NutrientsCalculatorController.class);

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    DayFoodPlanDAO dayFoodPlanDAO;

    @Autowired
    ProductWeightCalculationService calcService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome() {
        return "index";
    }

    @Cacheable("products")
    @RequestMapping(value = "/populateFoodItems", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public
    @ResponseBody
    List<Product> populateProducts() {
        return productsDAO.findAll();
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public
    @ResponseBody
    List<List<Pair<String, Double>>> calculate(@RequestBody DayFoodPlanRequest dayPlanRequest, HttpSession session) {

        if (Level.DEBUG.equals(LOG.getLevel())) {
            LOG.debug("Calculating weights for next products:");
            for (Product p : dayPlanRequest.getUniqueProducts()) {
                LOG.debug("\"" + p.getName() + "\", " + p.getkCal() + ", " + p.getProtein() + ", " + p.getFat() + ", " + p.getCarbo());
            }
        }

        Map<Product, Double> productsWithWeight = calcService.calcWeight(dayPlanRequest.getUniqueProducts(), dayPlanRequest.getConstrains());

        DayFoodPlan dayFoodPlan = DayFoodPlanHelper.hydrate(dayPlanRequest, productsWithWeight);

        saveDayFoodPlanIfUserExists(dayFoodPlan, (User) session.getAttribute("user"));

        return DayFoodPlanHelper.dehydrate(dayFoodPlan);
    }

    @RequestMapping(value = "/foodPlan", method = RequestMethod.POST)
    public
    @ResponseBody
    List<List<Pair<String, Double>>> foodPlan(@RequestBody LocalDateTime date, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            DayFoodPlan dayFoodPlan = dayFoodPlanDAO.findByUserIdAndDate(user.getId(), date);
            return DayFoodPlanHelper.dehydrate(dayFoodPlan);
        }
        LOG.error("No users logged in. DayFoodPlan will not be saved");
        return null;
    }

    private DayFoodPlan saveDayFoodPlanIfUserExists(DayFoodPlan dayFoodPlan, User user) {
        if (user != null) {
            DayFoodPlan previousDayFoodPlan = user.getDayFoodPlans().stream().filter(d -> dayFoodPlan.getDate().toLocalDate().equals(d.getDate().toLocalDate())).findFirst().orElse(null);
            if (previousDayFoodPlan != null) {
                user.getDayFoodPlans().remove(previousDayFoodPlan);
            }
            user.addDayFoodPlan(dayFoodPlan);
            userDAO.saveOrUpdate(user);
            LOG.info("DayFoodPlan for user " + user + " saved successfully");
        } else {
            LOG.error("No users logged in. DayFoodPlan will not be saved");
        }
        return dayFoodPlan;
    }
}
