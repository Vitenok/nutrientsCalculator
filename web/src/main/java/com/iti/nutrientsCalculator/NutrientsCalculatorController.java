package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.pudik.AmountItem;
import com.iti.foodCalculator.entity.pudik.CalculationInputDomainModel;
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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


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
    public @ResponseBody List<Map<String, Double>> calculate(@RequestBody List<List<Product>> productsList) {
        if (productsList.size() < 2) {
            return null;
        }

        List<Product> productsAll = productsList.stream().flatMap(l -> l.stream()).distinct().collect(Collectors.toList());
        List<Product> productsUnique = productsAll.stream().distinct().collect(Collectors.toList());

        Map<Product, Double> productsMap = productWeightCalculationService.calculateWeightOfProducts(productsUnique, productsUnique.remove(productsUnique.size() - 1));
        //TODO: Persist productsList

        productsList.remove(productsList.size()-1);
        List<Map<String, Double>> result = new ArrayList<>();
        for (List<Product> productList : productsList) {
            Map<String, Double> tmpResult = new HashMap<>();
            for (Product product : productList) {
                tmpResult.put(product.getName(), productsMap.get(product)/ Collections.frequency(productsAll, product));
            }
            result.add(tmpResult);
        }
        return result;
    }
}
