package com.iti.foodCalculator.service;

import com.iti.foodCalculator.entity.AmountItem;
import com.iti.foodCalculator.entity.DailyMacroelementsInput;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.entity.SupplementItem;
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

    @Before
    public void setUp() throws Exception {
        super.setUp();

        this.foodCalculatorService = new ProductWeightCalculatorService();
        this.productList = new ArrayList<Product>();
        this.supplementsList = new ArrayList<SupplementItem>();
        this.dailyMacroelementsInput = new DailyMacroelementsInput(0.0, 150, 25, 125);


        Product product1 = new Product("1", "Pangasius", 0.0, 14.9, 0.7, 0.0);
//        Product product2 = new Product("2", "Bokhvete", 0.0, 7.1, 2, 60.4);
        Product product3 = new Product("3", "Kesam", 0.0, 12, 1, 4.3);
        Product product4 = new Product("4", "Oil", 0.0, 0.2, 98, 0.0);

        productList.add(product1);
//        productList.add(product2);
        productList.add(product3);
        productList.add(product4);

        coefficients = new Array2DRowRealMatrix(new double[][]{
                {14.9,  12, 0.2},
                {0.7,  1, 98},
                {0,  4.3, 0}},
                false);
//        {14.9, 7.1, 12, 0.2},
//                {0.7, 2, 1, 98},
//                {0, 60.4, 4.3, 0}},
//                false);
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
        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(productList, supplementsList, dailyMacroelementsInput);

        assertNotNull(solution);
        assertEquals(4, solution.size());
        assertEquals(563.0, solution.get(0).getAmount());
        assertEquals("Pangasius", solution.get(0).getName());
        assertEquals(175.0, solution.get(1).getAmount());
        assertEquals("Bokhvete", solution.get(1).getName());
        assertEquals(447.0, solution.get(2).getAmount());
        assertEquals("Kesam", solution.get(2).getName());
        assertEquals(13.0, solution.get(3).getAmount());
        assertEquals("Oil", solution.get(3).getName());
    }

    public void testCalculateWeightOfProductsWithSupplements() {
        SupplementItem supplementItem1 = new SupplementItem("id", "Whey Protein", 0.0, 10, 2.5, 1);
        SupplementItem supplementItem2 = new SupplementItem("id", "Whey Protein", 0.0, 15, 2.5, 1);
        supplementsList.add(supplementItem1);
        supplementsList.add(supplementItem2);

        List<AmountItem> solution = foodCalculatorService.calculateWeightOfProducts(productList, supplementsList, dailyMacroelementsInput);

        assertNotNull(solution);
        assertEquals(4, solution.size());
        assertEquals(459.0, solution.get(0).getAmount());
        assertEquals("Pangasius", solution.get(0).getName());
        assertEquals(178.0, solution.get(1).getAmount());
        assertEquals("Bokhvete", solution.get(1).getName());
        assertEquals(367.0, solution.get(2).getAmount());
        assertEquals("Kesam", solution.get(2).getName());
        assertEquals(10.0, solution.get(3).getAmount());
        assertEquals("Oil", solution.get(3).getName());
    }

    public void testCorrectDailyMacroelementsInput() {
        SupplementItem supplementItem1 = new SupplementItem("id", "Whey Protein", 0.0, 10, 2.5, 1);
        SupplementItem supplementItem2 = new SupplementItem("id", "Whey Protein", 0.0, 15, 2.5, 1);
        supplementsList.add(supplementItem1);
        supplementsList.add(supplementItem2);

        DailyMacroelementsInput macroelementsInput = foodCalculatorService.correctDailyMacroelementsInput(dailyMacroelementsInput, supplementsList);
        DailyMacroelementsInput dummyDailyMacroelementsInput = new DailyMacroelementsInput(0, 150 - 25, 25 - 5, 125 - 2);

        assertEquals(dummyDailyMacroelementsInput.getProtein(), macroelementsInput.getProtein());
        assertEquals(dummyDailyMacroelementsInput.getFat(), macroelementsInput.getFat());
        assertEquals(dummyDailyMacroelementsInput.getCarb(), macroelementsInput.getCarb());
    }
}