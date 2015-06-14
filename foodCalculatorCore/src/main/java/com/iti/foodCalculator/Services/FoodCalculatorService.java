package com.iti.foodCalculator.Services;

import com.iti.foodCalculator.Entities.AmountItem;
import com.iti.foodCalculator.Entities.DailyMacroelementsInput;
import com.iti.foodCalculator.Entities.FoodItem;
import com.iti.foodCalculator.Entities.SupplementItem;
import org.apache.commons.math3.linear.*;

import java.util.ArrayList;
import java.util.List;

public class FoodCalculatorService {

    public List<AmountItem> calculateWeightOfFoodItems(List<FoodItem> chosenProducts, List<SupplementItem> chosenSupplementItems, DailyMacroelementsInput input) {
        RealVector constants = null;

        if (chosenSupplementItems.size() == 0) {
            constants = transformToConstantsVector(input);
        } else {
            constants = transformToConstantsVector(correctDailyMacroelementsInput(input, chosenSupplementItems));
        }

        RealMatrix coefficients = transformToCoefficientsMatrix(chosenProducts);
        DecompositionSolver solver = new SingularValueDecomposition(coefficients).getSolver();

        double[] solution = solver.solve(constants).toArray();

        List<AmountItem> amountItems = new ArrayList<AmountItem>();
        for (int i = 0; i < solution.length; i++) {
            solution[i] = Math.round((solution[i] * 100));
            AmountItem amountItem = new AmountItem(chosenProducts.get(i).getName(), solution[i]);
            amountItems.add(amountItem);
        }
        return amountItems;
    }

    public DailyMacroelementsInput correctDailyMacroelementsInput(DailyMacroelementsInput macroelementsInput, List<SupplementItem> supplementItemList) {
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

    public RealMatrix transformToCoefficientsMatrix(List<FoodItem> chosenProducts) {
        int size = chosenProducts.size();

        double proteins[] = new double[size];
        double fats[] = new double[size];
        double carbs[] = new double[size];

        for (int i = 0; i < chosenProducts.size(); i++) {
            proteins[i] = chosenProducts.get(i).getProtein();
            fats[i] = chosenProducts.get(i).getFat();
            carbs[i] = chosenProducts.get(i).getCarb();
        }
        return new Array2DRowRealMatrix(new double[][]{proteins, fats, carbs}, false);
    }

    public RealVector transformToConstantsVector(DailyMacroelementsInput dailyMacroelementsInput) {
        return new ArrayRealVector(new double[]{dailyMacroelementsInput.getProtein(), dailyMacroelementsInput.getFat(), dailyMacroelementsInput.getCarb()}, false);
    }
}
