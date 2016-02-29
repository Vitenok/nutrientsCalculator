package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("productWeightCalculationService")
public class ProductWeightCalculationService {

    private static final Logger LOG = LogManager.getLogger(ProductWeightCalculationService.class);

    private int maxMultiplier = 10;
    private int maxTimeToSearchInSeconds = 4;
    private double epsilon = 0.9;

    public ProductWeightCalculationService(int maxMultiplier, int maxTimeToSearchInSeconds, double epsilon) {
        this.maxMultiplier = maxMultiplier;
        this.maxTimeToSearchInSeconds = maxTimeToSearchInSeconds;
        this.epsilon = epsilon;
    }

    public ProductWeightCalculationService() {
    }

    public Map<Product, Double> calcWeight(List<Product> products, Product restriction) {

        Collections.sort(products, (o1, o2) -> Double.compare(o1.getProtein(), o2.getProtein()));

        StringBuilder bestSolution = new StringBuilder();

        long start = System.currentTimeMillis();
        calcWeight(products, restriction, new Product(restriction), "", new Product(restriction), bestSolution, 1, System.currentTimeMillis());
        LOG.debug("It took " + (System.currentTimeMillis() - start) + " ms to find solution: " + bestSolution);

        if (bestSolution.length() == 0) {
            LOG.info("No results found!");
            return new HashMap<>();
        }

        Map<Product, Double> result = reduceToMap(products, bestSolution.toString());

        if (LOG.getLevel().equals(Level.DEBUG)) {
            compareResultAndPrint(restriction, result);
        }

        return result;
    }

    private void calcWeight(List<Product> products,
                            Product globalRestriction,
                            Product currRestriction,
                            String currSolution,
                            Product bestRestriction,
                            StringBuilder bestSolution,
                            int multiplier,
                            long startTime) {
        if (System.currentTimeMillis() - startTime > maxTimeToSearchInSeconds * 1000) {
            LOG.debug("OUT OF TIME");
            return;
        }
        if (solutionIsCloseEnough(bestRestriction, epsilon)) {
            LOG.debug("SOLUTION FOUND!");
            LOG.debug("Best Restriction is " + bestRestriction);
            return;
        }
        for (int i = 0; i < products.size(); i++) {
            if (isMoreThen(currRestriction, products.get(i), multiplier)) {
                calcWeight(
                        products,
                        globalRestriction,
                        subtractProducts(currRestriction, products.get(i), multiplier),
                        currSolution + ";" + i + ":" + multiplier,
                        bestRestriction,
                        bestSolution,
                        multiplier == maxMultiplier ? multiplier : ++multiplier,
                        startTime);
            } else {
                double v = cKalDifference(globalRestriction, currRestriction);
                double v1 = cKalDifference(globalRestriction, bestRestriction);
                if (v > v1) {
                    LOG.debug("Solution " + currSolution + " is better than existed: " + bestSolution + " for " + (v - v1) + " kCal");
                    bestRestriction.setProtein(currRestriction.getProtein());
                    bestRestriction.setCarbo(currRestriction.getCarbo());
                    bestRestriction.setFat(currRestriction.getFat());
                    if (bestSolution.length() > 0) {
                        bestSolution.delete(0, bestSolution.length());
                    }
                    bestSolution.append(currSolution);
                }
            }
        }
    }

    private boolean solutionIsCloseEnough(Product bestRestriction, double epsilon) {
        return bestRestriction.getProtein() < epsilon &&
                bestRestriction.getCarbo() < epsilon &&
                bestRestriction.getFat() < epsilon;
    }

    private double cKalDifference(Product first, Product second) {
        return (first.getProtein() - second.getProtein() + first.getCarbo() - second.getCarbo()) * 4 + (first.getFat() - second.getFat()) * 9;
    }

    private Product subtractProducts(Product first, Product second, int multiplier) {
        return new Product(first.getProtein() - second.getProtein() / multiplier, first.getCarbo() - second.getCarbo() / multiplier, first.getFat() - second.getFat() / multiplier);
    }

    private boolean isMoreThen(Product first, Product second, int multiplier) {
        return first.getProtein() - second.getProtein() / multiplier >= 0 &&
                first.getCarbo() - second.getCarbo() / multiplier >= 0 &&
                first.getFat() - second.getFat() / multiplier >= 0;
    }

    private Map<Product, Double> reduceToMap(List<Product> products, String s) {
        Map<Product, Double> result = new HashMap<Product, Double>() {{
            for (Product p : products) {
                put(p, 0.0);
            }
        }};
        String[] split = s.substring(1).split(";", -1);
        for (String str : split) {
            String[] keyVal = str.split(":");
            int key = Integer.valueOf(keyVal[0]);
            int multiplier = Integer.valueOf(keyVal[1]);
            result.put(products.get(key), result.get(products.get(key)) + 100 / multiplier);
        }
        return result;
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
