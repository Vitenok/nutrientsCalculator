package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.AmountItem;
import com.iti.foodCalculator.entity.CalculationInputDomainModel;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.service.ProductWeightCalculatorService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Controller
public class NutrientsCalculatorController {
    private static final Logger LOG = LogManager.getLogger(NutrientsCalculatorController.class);

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    ProductWeightCalculatorService productWeightCalculatorService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome() {
        return "index";
    }

    @RequestMapping(value = "/loginUser", method = RequestMethod.GET)
    public void loginUser(@RequestParam String name, @RequestParam String socialNetwork, @RequestParam String token) {
        System.out.println(name+"logged in");
    }

    @Cacheable("products")
    @RequestMapping(value = "/populateFoodItems", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<Product> populateProducts() {
        return productsDAO.findAll();
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public @ResponseBody List<AmountItem> populateCalculation(@RequestBody CalculationInputDomainModel calculationInputDomainModel) {
        return productWeightCalculatorService.calculateWeightOfProducts(calculationInputDomainModel);
    }
}
