package com.iti.foodCalculator.service;

//import breeze.optimize.linear.NNLS;
import com.iti.foodCalculator.entity.Product;
import org.apache.commons.math3.linear.*;
import org.apache.spark.mllib.linalg.DenseMatrix;
import org.apache.spark.mllib.linalg.Matrices;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.mllib.optimization.NNLS;
import weka.classifiers.functions.pace.PaceMatrix;
import weka.core.matrix.DoubleVector;
import weka.core.matrix.IntVector;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

/**
 * Created by pkrasnom on 28.01.2016.
 */
public class SimpleProductWeightCalculatorService {

    public Map<Product, Double> calculateWeightOfProductsWithNNLS(List<Product> products, Product restriction) {


        DenseMatrix a = new DenseMatrix(products.size(), 3, getCoefficientsArray(products));
        DenseMatrix b = new DenseMatrix(3, 1, getDoubleConstants(restriction));

//        DenseMatrix at = a.transpose();
//        DenseMatrix ata = at.multiply(a);
//        DenseMatrix atb = at.multiply(b);

        double[] solution = NNLS.solve(a.toArray(), b.toArray(), new NNLS.Workspace(products.size()));

//        DoubleVector solution = new PaceMatrix(getCoefficients(products)).nnls(new PaceMatrix(new DoubleVector(getDoubleConstants(restriction))), new IntVector(products.size(), 0));

        Map<Product, Double> result = new HashMap<>();
        for (int i = 0; i<products.size(); i++) {
            result.put(products.get(i), 100*solution[i]);
        }
        return result;
    }

    public Map<Product, Double> calculateWeightOfProducts(List<Product> products, Product restriction) {

        RealMatrix coefficients = new Array2DRowRealMatrix(getCoefficients(products), false);
        SingularValueDecomposition singularValueDecomposition = new SingularValueDecomposition(coefficients);
        DecompositionSolver solver = singularValueDecomposition.getSolver();
        RealVector solution = solver.solve(new ArrayRealVector(getDoubleConstants(restriction), false));

        Map<Product, Double> result = new HashMap<>();
        for (int i = 0; i<products.size(); i++) {
            result.put(products.get(i), 100*solution.getEntry(i));
        }
        return result;
    }

    private double[] getCoefficientsArray(List<Product> chosenProducts) {

        double[] proteins = chosenProducts.stream().mapToDouble(value -> value.getProtein()).toArray();
        double[] fats = chosenProducts.stream().mapToDouble(value -> value.getFat()).toArray();
        double[] carbs = chosenProducts.stream().mapToDouble(value -> value.getCarbo()).toArray();

        return DoubleStream.concat(
        DoubleStream.concat(chosenProducts.stream().mapToDouble(value -> value.getProtein()),
                chosenProducts.stream().mapToDouble(value -> value.getFat())),
                chosenProducts.stream().mapToDouble(value -> value.getCarbo())).toArray();

    }


    private double[][] getCoefficients(List<Product> chosenProducts) {

        double[] proteins = chosenProducts.stream().mapToDouble(value -> value.getProtein()).toArray();
        double[] fats = chosenProducts.stream().mapToDouble(value -> value.getFat()).toArray();
        double[] carbs = chosenProducts.stream().mapToDouble(value -> value.getCarbo()).toArray();

        return new double[][]{proteins, fats, carbs};
    }

    private double[] getDoubleConstants(Product product) {
        return new double[]{product.getProtein(), product.getFat(), product.getCarbo()};
    }

}
