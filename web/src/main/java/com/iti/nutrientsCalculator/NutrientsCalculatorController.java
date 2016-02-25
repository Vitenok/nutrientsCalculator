package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.AmountItem;
import com.iti.foodCalculator.entity.CalculationInputDomainModel;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.service.ProductWeightCalculationService;
import com.iti.foodCalculator.service.ProductWeightCalculatorService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
public class NutrientsCalculatorController {
    private static final Logger LOG = LogManager.getLogger(NutrientsCalculatorController.class);

    @Autowired
    ProductsDAO productsDAO;

    @Autowired
    ProductWeightCalculatorService productWeightCalculatorService;

    @Autowired
    ProductWeightCalculationService productWeightCalculationService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome() {
        return "index";
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

    @RequestMapping(value = "/calc", method = RequestMethod.POST)
    public @ResponseBody Map<Integer, Double> calculate(@RequestBody List<Product> products) {
        if (products.size() < 2) {
            return null;
        }
        Map<Product, Double> productsMap = productWeightCalculationService.calculateWeightOfProducts(products, products.remove(products.size() - 1));
        Map<Integer, Double> result = new HashMap<>();
        for (Product p : products) {
            result.put(p.getId(), productsMap.get(p));
        }
        return result;
    }
}
