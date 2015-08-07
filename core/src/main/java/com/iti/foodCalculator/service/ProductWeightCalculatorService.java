package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.*;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.List;

public class ProductWeightCalculatorService {

    // TODO: results are < 0 --> suggest another food item with lacking macroelement; recalculate and ask for acceptance
    public List<AmountItem> calculateWeightOfProducts(CalculationInputDomainModel model) {
        List<Product> products = model.getProducts();
        List<SupplementItem> supplements= model.getSupplementItems();
        DailyMacroelementsInput dailyMacroelementsDistribution = model.getDailyMacroelementsInput();

        RealVector constants;

        if (supplements.size() == 0) {
            constants = transformToConstantsVector(dailyMacroelementsDistribution);
        } else {
            constants = transformToConstantsVector(correctDailyMacroelementsInput(dailyMacroelementsDistribution, supplements));
        }

        RealMatrix coefficients = transformToCoefficientsMatrix(products);
        DecompositionSolver solver = new SingularValueDecomposition(coefficients).getSolver();

        double[] solution = solver.solve(constants).toArray();

        List<AmountItem> amountItems = new ArrayList<AmountItem>();
        for (int i = 0; i < solution.length; i++) {
            solution[i] = Math.round((solution[i] * 100));
            AmountItem amountItem = new AmountItem(products.get(i).getItemName(), solution[i]);
            amountItems.add(amountItem);
        }
        return amountItems;
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
