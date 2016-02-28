package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.*;
import com.iti.foodCalculator.entity.pudik.AmountItem;
import com.iti.foodCalculator.entity.pudik.CalculationInputDomainModel;
import com.iti.foodCalculator.entity.pudik.DailyMacroelementsInput;
import com.iti.foodCalculator.entity.pudik.SupplementItem;
import org.apache.commons.math3.linear.*;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("productWeightCalculatorService")
public class ProductWeightCalculatorService {
    List<SupplementItem> supplements;
    DailyMacroelementsInput dailyMacroelementsDistribution;
    private List<Product> products;

    private static List<AmountItem> convertFromSupplementToAmountItem(CalculationInputDomainModel model) {
        List<AmountItem> supplements = new ArrayList<AmountItem>();
        List<SupplementItem> supplementItems = model.getSupplementItems();

        if (supplementItems != null) {
            for (int i = 0; i < supplementItems.size(); i++) {
                SupplementItem si = supplementItems.get(i);
                AmountItem am = new AmountItem(si.getName(), si.getWeight(), si.getProtein(), si.getFat(), si.getCarb(), si.getKcal());
                supplements.add(am);
            }
        }
        return supplements;
    }

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
                // TODO 1: There might be more coeff-s < 0. Do not remove all of them in 1 cycle; Remove 1 and recalculate.
                // TODO: or
                // TODO 2(probably better but more complex because aquations amount increase in geometr. progression): Find all coeff-s < 0; Create parallel equations without corresponding products and choose best solution ( if n elements<0 were found, => n equations);
                products.remove(n);
                solutionIterator.remove();
                CalculationInputDomainModel calculationInputDomainModelUpdate = new CalculationInputDomainModel();
                calculationInputDomainModelUpdate.setProducts(products);
                calculationInputDomainModelUpdate.setSupplementItems(supplements);
                calculationInputDomainModelUpdate.setDailyMacroelementsInput(dailyMacroelementsDistribution);
                return calculateSolutionMatrix(calculationInputDomainModelUpdate);
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
        double totalActualProtein = 0;
        double totalActualFat = 0;
        double totalActualCarb = 0;

        for (int i = 0; i < solutions.size(); i++) {
            Map nutrients = calculateNutrientValue(products.get(i), Math.round(solutions.get(i)));
            AmountItem amountItem = new AmountItem(products.get(i).getName(), Math.round(solutions.get(i)), (double) nutrients.get("Protein"), (double) nutrients.get("Fat"), (double) nutrients.get("Carb"), (double) nutrients.get("Calories"));
            amountItems.add(amountItem);
            totalActualCalories += amountItem.getTotalProtein() * 4 + amountItem.getTotalFat() * 9 + amountItem.getTotalCarb() * 4;
            totalActualProtein += amountItem.getTotalProtein();
            totalActualFat += amountItem.getTotalFat();
            totalActualCarb += amountItem.getTotalCarb();
        }

        // Recalculate if calories, fats, or carbs are > than in user's input
        if (totalActualFat > initFats * 1.15 && amountItems.size() > 1) {
            List<Product> initProds = model.getProducts();


            Collections.sort(initProds, new Comparator<Product>() {
                @Override
                public int compare(Product o1, Product o2) {
                    return Double.compare(o1.getFat(), o2.getFat());
                }
            });

            initProds.remove(initProds.size() - 1);
            model.setProducts(initProds);

            model.setDailyMacroelementsInput(new DailyMacroelementsInput(initCaloriesMax - 50, initProtein, initFats, initCarbs));
            return calculateWeightOfProducts(model);
        } else if (totalActualFat > initFats && amountItems.size() == 1) {
            AmountItem amountItem = amountItems.get(0);

            // Recalculate amount of single element based on daily input
            double oldAmount = amountItem.getAmount();
            double oldProt = amountItem.getTotalProtein();
            double oldCarb = amountItem.getTotalCarb();
            double oldFat = amountItem.getTotalFat();

            double updatedAmount = oldAmount * model.getDailyMacroelementsInput().getFat() / oldFat;

            double updatedFat = model.getDailyMacroelementsInput().getFat();
            double updatedProtein = updatedAmount * oldProt / oldAmount;
            double updatedCarb = updatedAmount * oldCarb / oldAmount;
            double updatedCalories = updatedFat * 9 + updatedCarb * 4 + updatedProtein * 4;

            amountItems.get(0).setAmount(Math.round(updatedAmount));
            amountItems.get(0).setTotalCalories(Math.round(updatedCalories));
            amountItems.get(0).setTotalCarb(Math.round(updatedCarb));
            amountItems.get(0).setTotalFat(Math.round(updatedFat));
            amountItems.get(0).setTotalProtein(Math.round(updatedProtein));
            amountItems.addAll(convertFromSupplementToAmountItem(model));
            return amountItems;
        } else {
            amountItems.addAll(convertFromSupplementToAmountItem(model));
            return amountItems;
        }
    }

    Map<String, Double> calculateNutrientValue(Product product, double amount) {
        double totalProtein = (double) Math.round(product.getProtein() * amount / 100);
        double totalFat = Math.round(product.getFat() * amount / 100);
        double totalCarb = Math.round(product.getCarbo() * amount / 100);

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
            proteins[i] = chosenProducts.get(i).getProtein();
            fats[i] = chosenProducts.get(i).getFat();
            carbs[i] = chosenProducts.get(i).getCarbo();
        }
        return new Array2DRowRealMatrix(new double[][]{proteins, fats, carbs}, false);
    }

    RealVector transformToConstantsVector(DailyMacroelementsInput dailyMacroelementsInput) {
        return new ArrayRealVector(new double[]{dailyMacroelementsInput.getProtein(), dailyMacroelementsInput.getFat(), dailyMacroelementsInput.getCarb()}, false);
    }
}
