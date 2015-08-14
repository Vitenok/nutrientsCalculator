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
        List<Double> solutions = calculateSolutionMatrix(model);

        List<AmountItem> amountItems = new ArrayList<>();

        for (int i = 0; i < solutions.size(); i++) {
            Map nutrients = calculateNutrientValue(products.get(i), Math.round(solutions.get(i)));
            AmountItem amountItem = new AmountItem(products.get(i).getItemName(), Math.round(solutions.get(i)), (double) nutrients.get("Protein"), (double) nutrients.get("Fat"), (double) nutrients.get("Carb"), (double) nutrients.get("Calories"));
            amountItems.add(amountItem);
        }
        return amountItems;
    }

    Map<String, Double> calculateNutrientValue(Product product, double amount) {
        double totalProtein = (double) Math.round(product.getProteins() * amount / 100);
        double totalFat = Math.round(product.getFats() * amount / 100);
        double totalCarb = Math.round(product.getCarbs() * amount / 100);
        double totalCal = Math.round(product.getkCal() * amount / 100);

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
