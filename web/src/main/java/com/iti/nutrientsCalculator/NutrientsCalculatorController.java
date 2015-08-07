package com.iti.nutrientsCalculator;

import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.utlity.reader.XLSReader;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class NutrientsCalculatorController {
    @RequestMapping(value = "/start.web", method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {

        return "index";
    }

    @RequestMapping(value = "/populateFoodItems", method = RequestMethod.GET, produces = { MediaType.APPLICATION_JSON_VALUE })
    public @ResponseBody
    List<Product> populateProducts() {

        return new XLSReader().readXlsFileToList();
    }
}
