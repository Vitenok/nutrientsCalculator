package com.iti.foodcalc.core.service;

import com.iti.foodcalc.core.entity.Product;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
public class ProductWeightCalculationService {

    private static final Logger LOG = LogManager.getLogger(ProductWeightCalculationService.class);

    @Value("${calculator.maxProductsPower}")
    private int maxProductsPower;
    @Value("${calculator.protein.caloriesInGram}")
    private int caloriesInProtein;
    @Value("${calculator.fat.caloriesInGram}")
    private int caloriesInFat;
    @Value("${calculator.carbohydrate.caloriesInGram}")
    private int caloriesInCarbohydrate;
    @Value("${calculator.epsilon}")
    private double epsilon;
    @Value("${calculator.maxTimeToCalculateMillis}")
    private int maxTimeToCalculateMillis;

    private int[][] ids;

    @PostConstruct
    private void postConstruct() {
        ids = new int[(int) Math.pow(2, maxProductsPower)][];
        for (int i = 0; i <= Math.pow(2, maxProductsPower) - 1; i++) {
            ids[i] = Arrays.asList(Integer.toBinaryString(i).split("")).stream().mapToInt(Integer::valueOf).toArray();
        }
    }

    public Map<Product, Double> calcWeight(List<Product> products, Product restriction) {

        if (products.isEmpty()) {
            return Collections.emptyMap();
        }

        double[] superProportion = new double[]{restriction.getProtein() * caloriesInProtein / restriction.getkCal(),
                restriction.getCarbo() * caloriesInCarbohydrate / restriction.getkCal(),
                restriction.getFat() * caloriesInFat / restriction.getkCal()};

        long start = System.currentTimeMillis();
        Map<List<Product>, Product> allCombos = buildCombosMap(products);
        Map.Entry<List<Product>, Product> mostProteinCombo = allCombos.entrySet().stream().sorted((a, b) -> Double.compare(density(b.getValue().getProtein(), b.getValue()), density(a.getValue().getProtein(), a.getValue()))).findFirst().get();
        Map.Entry<List<Product>, Product> mostCarbohydrateCombo = allCombos.entrySet().stream().sorted((a, b) -> Double.compare(density(b.getValue().getCarbo(), b.getValue()), density(a.getValue().getCarbo(), a.getValue()))).findFirst().get();
        Map.Entry<List<Product>, Product> mostFatCombo = allCombos.entrySet().stream().sorted((a, b) -> Double.compare(density(b.getValue().getFat(), b.getValue()), density(a.getValue().getFat(), a.getValue()))).findFirst().get();

        LOG.info("It took " + (System.currentTimeMillis() - start + "ms to build all combos map"));

        start = System.currentTimeMillis();

        Map<Product, Double> result = new HashMap<Product, Double>() {{
            products.stream().forEach(p -> put(p, 1.0));
        }};
        Product allProducts = allCombos.entrySet().stream().filter(e -> e.getKey().size() == products.size()).findFirst().get().getValue();
        Product aggregatedResult = new Product(allProducts);
        double[] currentProportion = new double[]{allProducts.getProtein() * caloriesInProtein / allProducts.getkCal(),
                allProducts.getCarbo() * caloriesInCarbohydrate / allProducts.getkCal(),
                allProducts.getFat() * caloriesInFat / allProducts.getkCal()};

        int iterations = 1;
        LOG.debug("Current proportion : " + Arrays.toString(currentProportion));
        while (!isProportionSuper(currentProportion, superProportion) && System.currentTimeMillis()-start < maxTimeToCalculateMillis) {
            if (Level.DEBUG.equals(LOG.getLevel())) {
                LOG.debug("Starting " + iterations + " iteration");
            }
            Map.Entry<List<Product>, Product> e = findBestComboMapEntry(allCombos, currentProportion, superProportion, aggregatedResult);
            if (e != null) {
                updateState(result, aggregatedResult, currentProportion, e);
            } else {
                int pcfIdx = -1;
                double delta = -1;
                for (int i = 0; i < currentProportion.length; i++) {
                    double abs = Math.abs(currentProportion[i] - superProportion[i]) / currentProportion[i];
                    if (abs > delta) {
                        delta = abs;
                        pcfIdx = i;
                    }
                }
                if (pcfIdx == 0) {
                    updateState(result, aggregatedResult, currentProportion, mostProteinCombo);
                    logShaking(currentProportion, mostProteinCombo, "protein");
                } else if (pcfIdx == 1) {
                    updateState(result, aggregatedResult, currentProportion, mostCarbohydrateCombo);
                    logShaking(currentProportion, mostCarbohydrateCombo, "carbohydrate");
                } else if (pcfIdx == 2) {
                    updateState(result, aggregatedResult, currentProportion, mostFatCombo);
                    logShaking(currentProportion, mostFatCombo, "fat");
                }
            }
            iterations++;
        }
        double k = restriction.getkCal() / aggregatedResult.getkCal();
        result.entrySet().forEach(e -> e.setValue(e.getValue() * k));

        LOG.info("It took " + (System.currentTimeMillis() - start) + " ms to find solution with " + iterations + " iterations");

        if (Level.DEBUG.equals(LOG.getLevel())) {
            compareResultAndPrint(restriction, result);
        }
        return result;
    }

    private void logShaking(double[] currentProportion, Map.Entry<List<Product>, Product> e, String name) {
        LOG.debug("Shaking a bit with more " + name);
        LOG.debug("     Better proportion " + Arrays.toString(currentProportion));
        e.getKey().stream().forEach(p -> LOG.debug("          " + p.getName()));
    }

    private double density(double element, Product p) {
        return element / (p.getProtein() * caloriesInProtein + (p.getCarbo() * caloriesInCarbohydrate) + (p.getFat() * caloriesInFat));
    }

    private Map<List<Product>, Product> buildCombosMap(List<Product> products) {
        Map<List<Product>, Product> result = new HashMap<>();
        for (int i = 0; i <= Math.pow(2, products.size()) - 1; i++) {
            List<Product> listByIds = listByIds(products, ids[i]);
            if (!listByIds.isEmpty()) {
                result.put(listByIds, buildAggregatedProduct(listByIds));
            }
        }
        return result;
    }

    private List<Product> listByIds(List<Product> products, int[] ids) {
        return new ArrayList<Product>() {{
            for (int i = 0; i < ids.length; i++) {
                if (ids[i] != 0) {
                    add(products.get(ids.length - i - 1));
                }
            }
        }};
    }

    private Product buildAggregatedProduct(List<Product> products) {
        double protein = 0;
        double carbohydrate = 0;
        double fat = 0;
        for (Product p : products) {
            protein += p.getProtein();
            carbohydrate += p.getCarbo();
            fat += p.getFat();
        }
        return new Product("", protein * caloriesInProtein + carbohydrate * caloriesInCarbohydrate + fat * caloriesInFat, protein, fat, carbohydrate);
    }

    private boolean isProportionSuper(double[] currentProportion, double[] superProportion) {
        for (int i = 0; i < superProportion.length; i++) {
            if (Math.abs(currentProportion[i] - superProportion[i]) > epsilon) {
                return false;
            }
        }
        return true;
    }

    private Map.Entry<List<Product>, Product> findBestComboMapEntry(Map<List<Product>, Product> allCombos, double[] currentProportion, double[] superProportion, Product currProduct) {
        double[] newProportion = new double[3];
        Map.Entry<List<Product>, Product> result = null;
        double currMinProportionDeltaSum = 0;
        for (int i = 0; i < newProportion.length; i++) {
            currMinProportionDeltaSum += Math.abs(currentProportion[i] - superProportion[i]);
        }
//        int iteration = 1;
        for (Map.Entry<List<Product>, Product> e : allCombos.entrySet()) {
            double newProportionDeltaSum = 0.0;
            updateTmpResult(newProportion, new Product(currProduct), e.getValue());
            for (int i = 0; i < newProportion.length; i++) {
                newProportionDeltaSum += Math.abs(newProportion[i] - superProportion[i]);
            }
            if (newProportionDeltaSum < currMinProportionDeltaSum) {
//                LOG.debug("     Better proportion " + Arrays.toString(newProportion) + " found on " + iteration + " out of " + allCombos.size());
//                e.getKey().stream().forEach(caloriesInProtein -> LOG.debug("          " + caloriesInProtein.getName()));
                currMinProportionDeltaSum = newProportionDeltaSum;
                result = e;
            }
//            iteration++;
        }
        return result;
    }

    private void updateState(Map<Product, Double> productsMap, Product aggregatedResult, double[] currentProportion, Map.Entry<List<Product>, Product> allCombos) {
        allCombos.getKey().stream().forEach(p -> productsMap.put(p, productsMap.get(p) + 1));
        updateTmpResult(currentProportion, aggregatedResult, allCombos.getValue());
    }

    private void updateTmpResult(double[] proportion, Product currProduct, Product currentCombo) {
        currProduct.setProtein(currProduct.getProtein() + currentCombo.getProtein());
        currProduct.setCarbo(currProduct.getCarbo() + currentCombo.getCarbo());
        currProduct.setFat(currProduct.getFat() + currentCombo.getFat());
        double kCal = currProduct.getProtein() * caloriesInProtein + currProduct.getCarbo() * caloriesInCarbohydrate + currProduct.getFat() * caloriesInFat;
        currProduct.setkCal(kCal / 100);
        proportion[0] = currProduct.getProtein() * caloriesInProtein / kCal;
        proportion[1] = currProduct.getCarbo() * caloriesInCarbohydrate / kCal;
        proportion[2] = currProduct.getFat() * caloriesInFat / kCal;
    }

    private void compareResultAndPrint(Product restriction, Map<Product, Double> result) {
        double calories = 0;
        double protein = 0;
        double carbohydrate = 0;
        double fat = 0;
        for (Product p : result.keySet()) {
            Double weight = result.get(p);
            double calcCalories = round(calcCalories(p));
            LOG.debug(p.getName() + " (PCF: " + p.getProtein() + ", " + p.getCarbo() + ", " + p.getFat() + "), kCal/100g: " + calcCalories + ", weight(g): " + weight);
            protein += p.getProtein() * caloriesInProtein * weight / 100;
            carbohydrate += p.getCarbo() * caloriesInCarbohydrate * weight / 100;
            fat += p.getFat() * caloriesInFat * weight / 100;
            calories += calcCalories * weight / 100;
        }
        LOG.debug("Total calories: " + round(calories));
        LOG.debug("PCF calculated(%): " + round(protein) + "(" + round(protein / calories) + "), " + round(carbohydrate) + "(" + round(carbohydrate / calories) + "), " + round(fat) + "(" + round(fat / calories) + ")");
        LOG.debug("PCF restriction(%): " + round(restriction.getProtein() * caloriesInProtein) + "(" + round(restriction.getProtein() * caloriesInProtein / restriction.getkCal()) + "), " + round(restriction.getCarbo() * caloriesInCarbohydrate) + "(" + round(restriction.getCarbo() * caloriesInCarbohydrate / restriction.getkCal()) + "), " + round(restriction.getFat() * caloriesInFat) + "(" + round(restriction.getFat() * caloriesInFat / restriction.getkCal()) + ")");
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
        return product.getProtein() * caloriesInProtein + product.getFat() * caloriesInFat + product.getCarbo() * caloriesInCarbohydrate;
    }
}
