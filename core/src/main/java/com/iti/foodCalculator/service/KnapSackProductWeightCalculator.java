package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KnapSackProductWeightCalculator {

    private static final Logger LOG = LogManager.getLogger(KnapSackProductWeightCalculator.class);

    private int maxMultiplier = 10;
    private int maxTimeToSearchInSeconds = 10;

    public KnapSackProductWeightCalculator(int maxMultiplier, int maxTimeToSearchInSeconds) {
        this.maxMultiplier = maxMultiplier;
        this.maxTimeToSearchInSeconds = maxTimeToSearchInSeconds;
    }

    public KnapSackProductWeightCalculator() {
    }

    public Map<Product, Double> calculateWeightOfProducts(List<Product> products, Product restriction) {

        Collections.sort(products, (o1, o2) -> Double.compare(o1.getProtein(), o2.getProtein()));

        StringBuilder bestSolution = new StringBuilder();

        long start = System.currentTimeMillis();
        calculateWeightOfProducts(products, restriction, new Product(restriction), "", new Product(restriction), bestSolution, 1, System.currentTimeMillis());
        LOG.debug("It took " + (System.currentTimeMillis() - start) + " ms to find solution: " + bestSolution);

        Map<Product, Double> result = reduceToMap(products, bestSolution.toString());

        compareResultAndPrint(restriction, result);

        return result;
    }

    private void compareResultAndPrint(Product restriction, Map<Product, Double> result) {
        double calories = 0;
        double protein = 0;
        double carbo = 0;
        double fat = 0;
        for (Product p : result.keySet()) {
            Double pCount = result.get(p);
            double calcCalories = round(calcCalories(p));
            LOG.debug(p.getName() + " (PCF: " + p.getProtein() + ", " + p.getCarbo() + ", " + p.getFat() + "), kCal/100g: " + calcCalories + ", count: " + pCount);
            protein = protein + p.getProtein()*4*pCount;
            carbo = carbo + p.getCarbo()*4*pCount;
            fat = fat + p.getFat()*9*pCount;
            calories = calories + calcCalories*pCount;
        }
        LOG.debug("Total calories: " + round(calories));
        LOG.debug("PCF(%): " + round(protein) + "(" + round(protein / calories) + "), " + round(carbo) + "(" + round(carbo / calories) + "), " + round(fat) + "(" + round(fat / calories) + ")");
        LOG.debug("PCF total(%): " + round(restriction.getProtein() * 4) + "(" + round(restriction.getProtein() * 4 / restriction.getkCal()) + "), " + round(restriction.getCarbo() * 4) + "(" + round(restriction.getCarbo() * 4 / restriction.getkCal()) + "), " + round(restriction.getFat() * 9) + "(" + round(restriction.getFat() * 9 / restriction.getkCal()) + ")");
    }

    public static double round(double value) {
        return round(value, 2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    private double calcCalories(Product product) {
        return product.getProtein()*4+product.getFat()*9+product.getCarbo()*4;
    }

    public void calculateWeightOfProducts(List<Product> products,
                                          Product globalRestriction,
                                          Product currRestriction,
                                          String currSolution,
                                          Product bestRestriction,
                                          StringBuilder bestSolution,
                                          int multiplier,
                                          long startTime) {
        if (System.currentTimeMillis()-startTime > maxTimeToSearchInSeconds*1000) {
            return;
        }
        for (int i=0; i<products.size(); i++) {
            if (isMoreThen(currRestriction, products.get(i), multiplier)){
                calculateWeightOfProducts(
                                        products,
                                        globalRestriction,
                                        subtractProducts(currRestriction, products.get(i), multiplier),
                                        currSolution+";"+i+":"+multiplier,
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
                    if (bestSolution.length()>0) {
                        bestSolution.delete(0, bestSolution.length());
                    }
                    bestSolution.append(currSolution);
                }
            }
        }
    }

    private double cKalDifference(Product first, Product second) {
        return (first.getProtein() - second.getProtein() + first.getCarbo() - second.getCarbo())*4 + (first.getFat() - second.getFat())*9;
    }

    private Product subtractProducts(Product first, Product second, int multiplier) {
        return new Product(first.getProtein() - second.getProtein()/multiplier, first.getCarbo() - second.getCarbo()/multiplier, first.getFat() - second.getFat()/multiplier);
    }

    private boolean isMoreThen(Product first, Product second, int multiplier) {
        return first.getProtein() - second.getProtein()/multiplier >= 0 &&
                first.getCarbo() - second.getCarbo()/multiplier >= 0 &&
                first.getFat() - second.getFat()/multiplier >= 0;
    }

    private Map<Product, Double> reduceToMap(List<Product> products, String s) {
        Map<Product, Double> result = new HashMap<Product, Double>(){{
            for (Product p : products) {
                put(p,0.0);
            }
        }};
        String[] split = s.substring(1).split(";", -1);
        for (String str : split) {
            String[] keyVal = str.split(":");
            int key = Integer.valueOf(keyVal[0]);
            int multiplier = Integer.valueOf(keyVal[1]);
            result.put(products.get(key), result.get(products.get(key)) + 1.0/multiplier);
        }
        return result;
    }
}
