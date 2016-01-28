package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.Product;
import org.apache.commons.math3.linear.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by pkrasnom on 28.01.2016.
 */
public class SimpleProductWeightCalculatorService {

    public Map<Product, Double> calculateWeightOfProducts(List<Product> products, Product restriction) {
        RealMatrix coefficients = transformToCoefficientsMatrix(products);

        SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(coefficients);

        DecompositionSolver solver = singularValueDecomposition.getSolver();

        RealVector solution = solver.solve(transformToConstantsVector(restriction));

        Map<Product, Double> result = new HashMap<>();
        for (int i = 0; i<products.size(); i++) {
            result.put(products.get(i), products.get(i).getEdiblePart()*solution.getEntry(i));
        }

        return result;
    }

    private RealMatrix transformToCoefficientsMatrix(List<Product> chosenProducts) {
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

    private RealVector transformToConstantsVector(Product product) {
        return new ArrayRealVector(new double[]{product.getProtein(), product.getFat(), product.getCarbo()}, false);
    }
}
