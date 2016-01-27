package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.entity.AmountItem;
import com.iti.foodCalculator.entity.CalculationInputDomainModel;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.service.ProductWeightCalculatorService;
import com.iti.foodCalculator.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
public class NutrientsCalculatorController {
    @Autowired
    ProductsService productsService;

    @Autowired
    ProductWeightCalculatorService productWeightCalculatorService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String printWelcome() {
        return "index";
    }

    @Cacheable("products")
    @RequestMapping(value = "/populateFoodItems", method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody List<Product> populateProducts() {
        return productsService.getAllProducts();
    }

    @RequestMapping(value = "/calculate", method = RequestMethod.POST)
    public @ResponseBody List<AmountItem> populateCalculation(@RequestBody CalculationInputDomainModel calculationInputDomainModel) {
        return productWeightCalculatorService.calculateWeightOfProducts(calculationInputDomainModel);
    }
}
