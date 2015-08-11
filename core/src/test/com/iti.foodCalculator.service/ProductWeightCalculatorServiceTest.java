package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.*;
import com.iti.foodCalculator.service.ProductWeightCalculatorService;
import junit.framework.TestCase;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class ProductWeightCalculatorServiceTest extends TestCase {
    ProductWeightCalculatorService foodCalculatorService;
    List<Product> productList;
    List<SupplementItem> supplementsList;
    RealMatrix coefficients;
    DailyMacroelementsInput dailyMacroelementsInput;
    CalculationInputDomainModel calculationInputDomainModel;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.foodCalculatorService = new ProductWeightCalculatorService();
        this.productList = new ArrayList<Product>();
        this.supplementsList = new ArrayList<SupplementItem>();
        this.dailyMacroelementsInput = new DailyMacroelementsInput(1200, 120, 26.6, 120);

//public Product(String name, double kcal, double protein, double fat, double carb, String productType) {
        Product product1 = new Product("Eggwhite", 42.0, 10.2, 0, 0.4, "123");
        Product product2 = new Product("Chick", 104.0, 23.8, 1, 0.0, "123");
        Product product3 = new Product("beef", 133, 21.1, 5.4, 0, "123");
        Product product4 = new Product("rice", 364, 9.4, 3, 72.8, "123");
        Product product5 = new Product("bread", 226, 9.8, 3.8, 35.2, "123");

        productList.add(product1);
        productList.add(product2);
        productList.add(product3);
        productList.add(product4);
        productList.add(product5);

        coefficients = new Array2DRowRealMatrix(new double[][]{
                {10.2, 23.8, 21.1, 9.4, 9.8},
                {0, 1, 5.4, 3, 3.8},
                {0.4, 0, 0, 72.8, 35.2}},
                false);

        calculationInputDomainModel = new CalculationInputDomainModel();
        calculationInputDomainModel.setProducts(productList);
        calculationInputDomainModel.setSupplementItems(new ArrayList<SupplementItem>());
        calculationInputDomainModel.setDailyMacroelementsInput(dailyMacroelementsInput);

    }

    public void testTransformToCoefficientsMatrix() throws Exception {

        RealMatrix matrix = foodCalculatorService.transformToCoefficientsMatrix(productList);

        assertEquals(coefficients, matrix);
    }

    public void testTransformToConstantsVector() {
        RealVector dummyConstants = new ArrayRealVector(new double[]{150, 25, 125}, false);
        RealVector constants = foodCalculatorService.transformToConstantsVector(dailyMacroelementsInput);

        assertNotNull(constants);
        assertEquals(dummyConstants, constants);
    }

    public void testCalculateWeightOfProductsWithNoSupplements() {
        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(calculationInputDomainModel);
        System.out.println(solution.get(0).getAmount());
        System.out.println(solution.get(1).getAmount());
        System.out.println(solution.get(2).getAmount());
        System.out.println(solution.get(3).getAmount());
        System.out.println(solution.get(4).getAmount());

        // TODO:
        // Problem: there is less calories in the solution then needed (~964 vs 1200)
        // 1. Calculate macronutrients in solution
        // 2. Suggest product which can fill this gap

        // TODO:
        // Problem: Solution may contain numbers < 0; Removing products does not influence others' amount ==> remove them from calculation

    }

//    public void testCalculateWeightOfProductsWithSupplements() {
//        SupplementItem supplementItem1 = new SupplementItem("id", "Whey Protein", 0.0, 10, 2.5, 1);
//        SupplementItem supplementItem2 = new SupplementItem("id", "Whey Protein", 0.0, 15, 2.5, 1);
//        supplementsList.add(supplementItem1);
//        supplementsList.add(supplementItem2);
//
//        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(productList, supplementsList, dailyMacroelementsInput);
//
//        assertNotNull(solution);
//        assertEquals(4, solution.size());
//        assertEquals(459.0, solution.get(0).getAmount());
//        assertEquals("Pangasius", solution.get(0).getName());
//        assertEquals(178.0, solution.get(1).getAmount());
//        assertEquals("Bokhvete", solution.get(1).getName());
//        assertEquals(367.0, solution.get(2).getAmount());
//        assertEquals("Kesam", solution.get(2).getName());
//        assertEquals(10.0, solution.get(3).getAmount());
//        assertEquals("Oil", solution.get(3).getName());
//    }
//
//    public void testCorrectDailyMacroelementsInput() {
//        SupplementItem supplementItem1 = new SupplementItem("id", "Whey Protein", 0.0, 10, 2.5, 1);
//        SupplementItem supplementItem2 = new SupplementItem("id", "Whey Protein", 0.0, 15, 2.5, 1);
//        supplementsList.add(supplementItem1);
//        supplementsList.add(supplementItem2);
//
//        DailyMacroelementsInput macroelementsInput = foodCalculatorService.correctDailyMacroelementsInput(dailyMacroelementsInput, supplementsList);
//        DailyMacroelementsInput dummyDailyMacroelementsInput = new DailyMacroelementsInput(0, 150 - 25, 25 - 5, 125 - 2);
//
//        assertEquals(dummyDailyMacroelementsInput.getProtein(), macroelementsInput.getProtein());
//        assertEquals(dummyDailyMacroelementsInput.getFat(), macroelementsInput.getFat());
//        assertEquals(dummyDailyMacroelementsInput.getCarb(), macroelementsInput.getCarb());
//    }
}