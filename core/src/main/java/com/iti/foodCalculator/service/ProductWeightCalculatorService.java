package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.*;
import org.apache.commons.math3.linear.*;

import java.util.*;

public class ProductWeightCalculatorService {
    private List<Product> products;
    List<SupplementItem> supplements;
    DailyMacroelementsInput dailyMacroelementsDistribution;
    int x = 0;

    List<Double> calculateSolutionMatrix(CalculationInputDomainModel model) {

        products = model.getProducts();
        supplements = model.getSupplementItems();
        dailyMacroelementsDistribution = model.getDailyMacroelementsInput();

        RealVector constants;

        if (supplements.size() == 0) {
            constants = transformToConstantsVector(dailyMacroelementsDistribution);
        } else {
            constants = transformToConstantsVector(correctDailyMacroelementsInput(dailyMacroelementsDistribution, supplements));
        }

        RealMatrix coefficients = transformToCoefficientsMatrix(products);

        SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(coefficients);

        DecompositionSolver solver = singularValueDecomposition.getSolver();

        double[] solution = solver.solve(constants).toArray();


        List<Double> solutionList = new ArrayList<Double>();
        for (int i = 0; i < solution.length; i++) {
            solutionList.add(solution[i]);
        }
        ListIterator<Double> solutionIterator = solutionList.listIterator();
        int n = 0;
        int initLength = products.size();

        // If solution contains numbers < 0, remove them and corresponding product
        while (solutionIterator.hasNext()) {
            Double currentSolution = solutionIterator.next();
            solutionIterator.set(currentSolution * 100);

            if (currentSolution < 0) {
                products.remove(n);
                solutionIterator.remove();
            } else {
                n++;
            }
        }

        // If some items were deleted recalculate solution
        if (n == initLength) {
            return solutionList;
        } else {
            CalculationInputDomainModel calculationInputDomainModelUpdate = new CalculationInputDomainModel();
            calculationInputDomainModelUpdate.setProducts(products);
            calculationInputDomainModelUpdate.setSupplementItems(supplements);
            calculationInputDomainModelUpdate.setDailyMacroelementsInput(dailyMacroelementsDistribution);
            return calculateSolutionMatrix(calculationInputDomainModelUpdate);
        }
    }

    public List<AmountItem> calculateWeightOfProducts(CalculationInputDomainModel model) {
        Double initCaloriesMax = model.getDailyMacroelementsInput().getKcal();
        Double initProtein = model.getDailyMacroelementsInput().getProtein();
        Double initFats = model.getDailyMacroelementsInput().getFat();
        Double initCarbs = model.getDailyMacroelementsInput().getCarb();

        List<Double> solutions = calculateSolutionMatrix(model);
        List<AmountItem> amountItems = new ArrayList<AmountItem>();

        double totalActualCalories = 0;
        double totalActualFat = 0;
        double totalActualCarb = 0;

        for (int i = 0; i < solutions.size(); i++) {
            Map nutrients = calculateNutrientValue(products.get(i), Math.round(solutions.get(i)));
            AmountItem amountItem = new AmountItem(products.get(i).getItemName(), Math.round(solutions.get(i)), (double) nutrients.get("Protein"), (double) nutrients.get("Fat"), (double) nutrients.get("Carb"), (double) nutrients.get("Calories"));
            amountItems.add(amountItem);

            totalActualCalories += amountItem.getTotalProtein() * 4 + amountItem.getTotalFat() * 9 + amountItem.getTotalCarb() * 4;
            totalActualFat += amountItem.getTotalFat();
            totalActualCarb += amountItem.getTotalCarb();
        }

        // Recalculate if calories, fats, or carbs are > than in user input
        if (totalActualCalories > initCaloriesMax || totalActualFat > initFats || totalActualCarb > initCarbs) {
            model.setDailyMacroelementsInput(new DailyMacroelementsInput(initCaloriesMax - 50, initProtein, initFats, initCarbs));
            return calculateWeightOfProducts(model);
        } else {
            return amountItems;
        }
    }

    Map<String, Double> calculateNutrientValue(Product product, double amount) {
        double totalProtein = (double) Math.round(product.getProteins() * amount / 100);
        double totalFat = Math.round(product.getFats() * amount / 100);
        double totalCarb = Math.round(product.getCarbs() * amount / 100);

        // for some products calories are wrong in the DB. Solution: recalculate total kCal self :(
        double totalCal = totalProtein * 4 + totalFat * 9 + totalCarb * 4;

        Map<String, Double> map = new HashMap<String, Double>();
        map.put("Protein", totalProtein);
        map.put("Fat", totalFat);
        map.put("Carb", totalCarb);
        map.put("Calories", totalCal);

        return map;
    }

    DailyMacroelementsInput correctDailyMacroelementsInput(DailyMacroelementsInput macroelementsInput, List<SupplementItem> supplementItemList) {
        double prots = 0;
        double fats = 0;
        double carbs = 0;

        for (SupplementItem supplementItem : supplementItemList) {
            prots += supplementItem.getProtein();
            fats += supplementItem.getFat();
            carbs += supplementItem.getCarb();
        }

        prots = macroelementsInput.getProtein() - prots;
        fats = macroelementsInput.getFat() - fats;
        carbs = macroelementsInput.getCarb() - carbs;

        macroelementsInput.setProtein(prots);
        macroelementsInput.setFat(fats);
        macroelementsInput.setCarb(carbs);

        return macroelementsInput;
    }

    RealMatrix transformToCoefficientsMatrix(List<Product> chosenProducts) {
        int size = chosenProducts.size();

        double proteins[] = new double[size];
        double fats[] = new double[size];
        double carbs[] = new double[size];

        for (int i = 0; i < chosenProducts.size(); i++) {
            proteins[i] = chosenProducts.get(i).getProteins();
            fats[i] = chosenProducts.get(i).getFats();
            carbs[i] = chosenProducts.get(i).getCarbs();
        }
        return new Array2DRowRealMatrix(new double[][]{proteins, fats, carbs}, false);
    }

    RealVector transformToConstantsVector(DailyMacroelementsInput dailyMacroelementsInput) {
        return new ArrayRealVector(new double[]{dailyMacroelementsInput.getProtein(), dailyMacroelementsInput.getFat(), dailyMacroelementsInput.getCarb()}, false);
    }
}
