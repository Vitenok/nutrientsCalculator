package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("productWeightCalculationService")
public class ProductWeightCalculationService {

    private static final Logger LOG = LogManager.getLogger(ProductWeightCalculationService.class);
    public static final String KEY_VAL_DELIM = ":";
    public static final String PAIR_DELIM = ";";

    private int maxMultiplier = 10;
    private int maxTimeToSearchInSeconds = 4;
    private double epsilon = 20;

    public ProductWeightCalculationService(int maxMultiplier, int maxTimeToSearchInSeconds, double epsilon) {
        this.maxMultiplier = maxMultiplier;
        this.maxTimeToSearchInSeconds = maxTimeToSearchInSeconds;
        this.epsilon = epsilon;
    }

    public ProductWeightCalculationService() {
    }

    public Map<Product, Double> calcWeight(List<Product> products, Product restriction) {
        Map<Product, Double> result = new HashMap<>();

        int n = 50;
        List<String> tmpSolutions = new ArrayList<>();

        long start = System.currentTimeMillis();
        for (Product p : products) {
            tmpSolutions = addSolution(tmpSolutions, p, n);
        }

        /**
        double[] bestSolution = new double[]{restriction.getProtein()*4/restriction.getkCal(),
                                             restriction.getCarbo()*4/restriction.getkCal(),
                                             restriction.getFat()*9/restriction.getkCal()};
        **/
        int currSolIdx;
        double[] currSol;
        for (String tmpSol : tmpSolutions) {
            String[] split = tmpSol.split(PAIR_DELIM);
            double protein = 0;
            double carbo = 0;
            double fat = 0;
            for (String str : split) {
                String[] keyVal = str.split(KEY_VAL_DELIM);
                Product p = products.stream().filter(product -> product.getId() == Integer.valueOf(keyVal[0])).findFirst().get();
                double multiplier = Double.valueOf(keyVal[1]);
                protein=+p.getProtein()*multiplier/100;
                carbo=+p.getCarbo()*multiplier/100;
                fat=+p.getFat()*multiplier/100;
            }
            if (Math.abs(restriction.getProtein()-protein)<epsilon*4 && Math.abs(restriction.getCarbo()-carbo)<epsilon*4 && Math.abs(restriction.getFat() - fat)<epsilon*9) {
                for (String str : split) {
                    String[] keyVal = str.split(KEY_VAL_DELIM);
                    Product p = products.stream().filter(product -> product.getId() == Integer.valueOf(keyVal[0])).findFirst().get();
                    double multiplier = Integer.valueOf(keyVal[1]);
                    result.put(p, multiplier);
                    break;
                }
            }
        }
        LOG.debug("It took " + (System.currentTimeMillis() - start) + " ms to find solution");

        if (Level.DEBUG.equals(LOG.getLevel())) {
            compareResultAndPrint(restriction, result);
        }
        return result;
    }

    private List<String> addSolution(List<String> prevSolutions, Product product, int combinations) {
        List<String> newSolutions = new ArrayList<>();
        if (prevSolutions.isEmpty()) {
            for (int i = 40; i <= combinations; i++) {
                newSolutions.add(product.getId() + KEY_VAL_DELIM + i);
            }
            return newSolutions;
        }
        for (int i = 40; i <= combinations; i++) {
            for (int j = 0; j < prevSolutions.size() ; j++) {
                newSolutions.add(prevSolutions.get(j) + PAIR_DELIM + product.getId() + KEY_VAL_DELIM + i);
            }
        }
        return newSolutions;
    }

    private void compareResultAndPrint(Product restriction, Map<Product, Double> result) {
        double calories = 0;
        double protein = 0;
        double carbo = 0;
        double fat = 0;
        for (Product p : result.keySet()) {
            Double weight = result.get(p);
            double calcCalories = round(calcCalories(p));
            LOG.debug(p.getName() + " (PCF: " + p.getProtein() + ", " + p.getCarbo() + ", " + p.getFat() + "), kCal/100g: " + calcCalories + ", weight(g): " + weight);
            protein = protein + p.getProtein() * 4 * weight / 100;
            carbo = carbo + p.getCarbo() * 4 * weight / 100;
            fat = fat + p.getFat() * 9 * weight / 100;
            calories = calories + calcCalories * weight / 100;
        }
        LOG.debug("Total calories: " + round(calories));
        LOG.debug("PCF(%): " + round(protein) + "(" + round(protein / calories) + "), " + round(carbo) + "(" + round(carbo / calories) + "), " + round(fat) + "(" + round(fat / calories) + ")");
        LOG.debug("PCF total(%): " + round(restriction.getProtein() * 4) + "(" + round(restriction.getProtein() * 4 / restriction.getkCal()) + "), " + round(restriction.getCarbo() * 4) + "(" + round(restriction.getCarbo() * 4 / restriction.getkCal()) + "), " + round(restriction.getFat() * 9) + "(" + round(restriction.getFat() * 9 / restriction.getkCal()) + ")");
    }

    private double round(double value) {
        return round(value, 2);
    }

    private double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double calcCalories(Product product) {
        return product.getProtein() * 4 + product.getFat() * 9 + product.getCarbo() * 4;
    }
}
