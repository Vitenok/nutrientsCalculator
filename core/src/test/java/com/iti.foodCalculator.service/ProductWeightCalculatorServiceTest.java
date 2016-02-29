package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.*;
import com.iti.foodCalculator.entity.pudik.AmountItem;
import com.iti.foodCalculator.entity.pudik.CalculationInputDomainModel;
import com.iti.foodCalculator.entity.pudik.DailyMacroelementsInput;
import com.iti.foodCalculator.entity.pudik.SupplementItem;
import junit.framework.TestCase;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Before;

import java.util.ArrayList;
import java.util.List;

public class ProductWeightCalculatorServiceTest extends TestCase {
    ProductWeightCalculatorService foodCalculatorService;
    List<Product> invalidProductList;
    List<Product> semiValidProductList;
    List<Product> validProductList;
    List<SupplementItem> supplementsList;
    RealMatrix coefficients;
    DailyMacroelementsInput dailyMacroelementsInput;
    CalculationInputDomainModel calculationInputDomainModel;
    CalculationInputDomainModel invalidCalculationInputDomainModel;
    CalculationInputDomainModel semiValidCalculationInputDomainModel;

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.foodCalculatorService = new ProductWeightCalculatorService();
        this.validProductList = new ArrayList<Product>();
        this.invalidProductList = new ArrayList<Product>();
        this.semiValidProductList = new ArrayList<Product>();
        this.supplementsList = new ArrayList<SupplementItem>();
        double prot = 1200 * 0.4 / 4;
        double fat = 1200 * 0.2 / 9;
        double carb = 1200 * 0.4 / 4;
        this.dailyMacroelementsInput = new DailyMacroelementsInput(1200, prot, fat, carb);
        //this.dailyMacroelementsInput = new DailyMacroelementsInput(1200, 120, 26.6, 120);

        Product product1 = new Product("Egg white, raw", 42.0, 10.2, 0, 0.4);
        Product product2 = new Product("Chicken, breast, meat only, raw", 104.0, 23.8, 1, 0.0);
        Product product3 = new Product("Beef, minced meat, max 6 % fat, raw", 133, 21.1, 5.4, 0);
        Product product4 = new Product("Rice, brown, long-grain, dry", 364, 9.4, 3, 72.8);
        Product product5 = new Product("Bread, wholemeal with extra fiber (75-100 %), unspecified, industry made", 226, 9.8, 3.8, 35.2);
        Product product6 = new Product("Cheese, hard, Cheddar", 395, 25.7, 32.3, 0.3);

        // For invalid output
        Product product7 = new Product("Oil, olive", 892, 0.2, 99, 0);

        //  Non-fittable
        Product product8 = new Product("Bread, 1/3 wholemeal flour, water, home made", 236, 8.4, 1.3, 45.5);

        // Valid input
        validProductList.add(product1);
        validProductList.add(product2);
        validProductList.add(product3);
        validProductList.add(product4);
        validProductList.add(product5);
        validProductList.add(product6);

        // Invalid multiple input when solution returns  < 0
        invalidProductList.add(product1);
        invalidProductList.add(product2);
        invalidProductList.add(product3);
        invalidProductList.add(product4);
        invalidProductList.add(product5);
        invalidProductList.add(product6);
//        invalidProductList.add(product7);
//        invalidProductList.add(product8);

        coefficients = new Array2DRowRealMatrix(new double[][]{
                {10.2, 23.8, 21.1, 9.4, 9.8, 25.7}, // Proetins
                {0, 1, 5.4, 3, 3.8, 32.3}, // Fats
                {0.4, 0, 0, 72.8, 35.2, 0.3}}, // Carbs
                false);

        // Valid
        calculationInputDomainModel = new CalculationInputDomainModel();
        calculationInputDomainModel.setProducts(validProductList);
        calculationInputDomainModel.setSupplementItems(new ArrayList<SupplementItem>());
        calculationInputDomainModel.setDailyMacroelementsInput(dailyMacroelementsInput);

        // Invalid
        invalidCalculationInputDomainModel = new CalculationInputDomainModel();
        invalidCalculationInputDomainModel.setProducts(invalidProductList);
        invalidCalculationInputDomainModel.setSupplementItems(new ArrayList<>());
        invalidCalculationInputDomainModel.setDailyMacroelementsInput(dailyMacroelementsInput);
    }

    public void testMatrix() {
        List sol = foodCalculatorService.calculateSolutionMatrix(calculationInputDomainModel);
        for (int i = 0; i < sol.size(); i++) {
            System.out.println(i + ": " + sol.get(i));
        }
    }

    public void testTransformToCoefficientsMatrix() throws Exception {
        RealMatrix matrix = foodCalculatorService.transformToCoefficientsMatrix(validProductList);
        assertEquals(coefficients, matrix);
    }

    // Non-fittable
    public void testSemiValidProductList() {
        // Product list which can not be fitted in macronutrients vector
        Product product9 = new Product("Beef, rib, cube roll, raw", 200, 20.5, 13.1, 0);
        Product product10 = new Product("Casserole, beef (10 % fat), potatoes and vegetables", 95, 9.3, 4.3, 4.4);
        Product product11 = new Product("Bread, semi wholemeal (25-50 %), type Yoghurtbrød", 222, 8.3, 1.5, 41.7);

        semiValidProductList.add(product9);
        semiValidProductList.add(product10);
        semiValidProductList.add(product11);

        List<Product> referenceList = new ArrayList<Product>();
        referenceList.add(product10);
        referenceList.add(product11);

        CalculationInputDomainModel refModel = new CalculationInputDomainModel();
        refModel.setProducts(referenceList);
        refModel.setDailyMacroelementsInput(dailyMacroelementsInput);
        refModel.setSupplementItems(new ArrayList<>());

        // 1 product
        semiValidCalculationInputDomainModel = new CalculationInputDomainModel();
        semiValidCalculationInputDomainModel.setProducts(semiValidProductList);
        semiValidCalculationInputDomainModel.setSupplementItems(new ArrayList<>());
        semiValidCalculationInputDomainModel.setDailyMacroelementsInput(dailyMacroelementsInput);

        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(semiValidCalculationInputDomainModel);
        List<AmountItem> refSolution = foodCalculatorService.calculateWeightOfProducts(refModel);

        assertNotNull(solution);
        assertNotNull(refSolution);

        assertEquals(solution.get(0).getAmount(), refSolution.get(0).getAmount());
    }

    public void testCalculateWeightOfProductsWithNoSupplements() {
        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(calculationInputDomainModel);
        List<Double> amounts = new ArrayList<>();
        amounts.add(solution.get(0).getAmount());
        amounts.add(solution.get(1).getAmount());
        amounts.add(solution.get(2).getAmount());
        amounts.add(solution.get(3).getAmount());
        amounts.add(solution.get(4).getAmount());
        amounts.add(solution.get(5).getAmount());
    }

    public void testThatProblematicFoodItemDoesNotChangeSolution() {
        List<AmountItem> originalSolution = foodCalculatorService.calculateWeightOfProducts(calculationInputDomainModel);
        List<AmountItem> problematicSolution = foodCalculatorService.calculateWeightOfProducts(invalidCalculationInputDomainModel);

        assertNotNull(originalSolution);
        assertNotNull(problematicSolution);

        assertEquals(originalSolution.get(0).getAmount(), problematicSolution.get(0).getAmount());
    }
}

/*    public void testCalculateWeightOfProductsWithNoSupplementsOneProduct() {
        CalculationInputDomainModel singleModel = new CalculationInputDomainModel();
        List<Product> singleList = new ArrayList<Product>();
        singleList.add(productList.get(0));
        singleList.add(productList.get(4));

        singleModel.setProducts(singleList);
        singleModel.setSupplementItems(new ArrayList<SupplementItem>());
        singleModel.setDailyMacroelementsInput(dailyMacroelementsInput);

        List<AmountItem> solution = foodCalculatorService.calcWeight(singleModel);

        List<Double> amounts = new ArrayList<>();
        amounts.add(solution.get(0).getAmount());
        amounts.add(solution.get(1).getAmount());

        List<Double> nutrients = foodCalculatorService.calculateNutrientValues(singleList, amounts);
        for (int i = 0; i < nutrients.size(); i++) {
            System.out.println(i + ": " + nutrients.get(i));
        }
        System.out.println("");
        System.out.println(solution.get(0).getAmount());
        System.out.println(solution.get(1).getAmount());
    }
*/

//    public void testCalculateWeightOfProductsWithSupplements() {
//        SupplementItem supplementItem1 = new SupplementItem("id", "Whey Protein", 0.0, 10, 2.5, 1);
//        SupplementItem supplementItem2 = new SupplementItem("id", "Whey Protein", 0.0, 15, 2.5, 1);
//        supplementsList.add(supplementItem1);
//        supplementsList.add(supplementItem2);
//
//        List<AmountItem> solution = foodCalculatorService.calcWeight(productList, supplementsList, dailyMacroelementsInput);
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
//}