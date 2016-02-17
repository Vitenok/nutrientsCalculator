package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkrasnom on 15.02.2016.
 */
public class KnapSackProductWeightCalculator {

    private static int MULTIPLIER = 10;

    public Map<Product, Double> calculateWeightOfProducts(List<Product> products, Product restriction) {

        HashMap<Product, Double> result = new HashMap<Product, Double>() {{
            for (Product p : products) {
                put(p, 0.0);
            }
        }};
        StringBuilder bestSolution = new StringBuilder();

        long start = System.currentTimeMillis();
        calculateWeightOfProducts(products, restriction, new Product(restriction), "", new Product(restriction), bestSolution);
        System.out.println("It took " + (System.currentTimeMillis()-start) + " ms to find solution.");

        System.out.println("Solution: " + bestSolution);
        compareResultAndPrint(restriction, products, bestSolution.toString());

        return result;
    }

    private void compareResultAndPrint(Product restriction, List<Product> products, String s) {
        Map<Product, Double> reduced = reduceToMap(products, s);
        double calories = 0;
        double protein = 0;
        double carbo = 0;
        double fat = 0;
        for (Product p : products) {
            Double pCount = reduced.get(p)/MULTIPLIER;
            double calcCalories = round(calcCalories(p));
            System.out.println(p.getName() + " (PCF: " + p.getProtein() + ", " + p.getCarbo() + ", " + p.getFat() + "), kCal/100g: " + calcCalories + ", count: " + pCount);
            protein = protein + p.getProtein()*4*pCount;
            carbo = carbo + p.getCarbo()*4*pCount;
            fat = fat + p.getFat()*9*pCount;
            calories = calories + calcCalories*pCount;
        }
        System.out.println("Total calories: " + round(calories));
        System.out.println("PCF(%): " + round(protein) + "("+round(protein/calories)+"), " + round(carbo) + "("+round(carbo/calories)+"), " + round(fat)+"("+round(fat/calories)+")");
        System.out.println("PCF total(%): " + round(restriction.getProtein()*4) + "("+round(restriction.getProtein()*4/restriction.getkCal())+"), " + round(restriction.getCarbo()*4) + "("+round(restriction.getCarbo()*4/restriction.getkCal())+"), " + round(restriction.getFat()*9)+"("+round(restriction.getFat()*9/restriction.getkCal())+")");
        System.out.println();
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

    private Map<Product, Double> reduceToMap(List<Product> products, String s) {
        Map<Product, Double> result = new HashMap<>();
        for (int i=0; i<products.size(); i++) {
            result.put(products.get(i), (double)s.split(String.valueOf(i),-1).length-1);
        }
        return result;
    }

    public void calculateWeightOfProducts(List<Product> products,
                                          Product globalRestriction,
                                          Product currRestriction,
                                          String currSolution,
                                          Product bestRestriction,
                                          StringBuilder bestSolution) {
        for (int i=0; i<products.size(); i++) {
            if (isMoreThen(currRestriction, products.get(i))){
                calculateWeightOfProducts(products, globalRestriction, subtractProducts(currRestriction, products.get(i)), currSolution+";"+i, bestRestriction, bestSolution);
            } else {
                double v = cKalDifference(globalRestriction, currRestriction);
                double v1 = cKalDifference(globalRestriction, bestRestriction);
                if (v > v1) {
//                if (isMoreThen(bestRestriction, currRestriction)) {
                    System.out.println("Solution " + currSolution + " is better than existed: " + bestSolution + " for " + (v-v1) + " kCal");
//                    Solution: ;0;1;1;3;2;2;2
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

    private Product subtractProducts(Product first, Product second) {
        return new Product(first.getProtein() - second.getProtein()/MULTIPLIER, first.getCarbo() - second.getCarbo()/MULTIPLIER, first.getFat() - second.getFat()/MULTIPLIER);
    }

    private boolean isMoreThen(Product first, Product second) {
        return first.getProtein() - second.getProtein()/MULTIPLIER >= 0 &&
                first.getCarbo() - second.getCarbo()/MULTIPLIER >= 0 &&
                first.getFat() - second.getFat()/MULTIPLIER >= 0;
    }
}
