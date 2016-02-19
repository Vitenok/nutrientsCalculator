package com.iti.foodCalculator.utlity.reader;

import com.iti.foodCalculator.dao.ProductsDAO;
import com.iti.foodCalculator.entity.Category;
import com.iti.foodCalculator.entity.Product;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSImporter {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("import-application-context.xml");
        ProductsDAO dao = context.getBean("productsDAO", ProductsDAO.class);
        List<Product> products = new XLSImporter().readXlsFileToList();
        dao.save(products);
        System.out.println("Imported successfully");
    }

    public List<Product> readXlsFileToList() {

        List<Product> productsList = new ArrayList<>();
        try {
            String fileName = getClass().getClassLoader().getResource("NorwegianFoodComposition/TheNorwegianFoodCompostionTable2015.xlsx").getFile();
            FileInputStream file = new FileInputStream(new File(fileName));

            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            Category parent = new Category("Food", null);

            int level = 0;
            int newLevel;

            for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
                Row row = rowIterator.next();

                if (row.getRowNum() < 5) {
                    continue;
                }

                if (row.getRowNum() > 1700) {
                    break;
                }

                if (row.getCell(1).toString().length() != 0) {
                    if (isValidCategoryRow(row)) { // valid = text is bold
                        row.getCell(0).setCellType(Cell.CELL_TYPE_STRING); // because some of them are in numeric format

                        newLevel = getCurrentLevel(row.getCell(0).getStringCellValue());
                        String name = row.getCell(1).getStringCellValue();

                        if (newLevel > level) { // child
                            parent = parent.getChildren().get(parent.getChildren().size() - 1);
                            level = newLevel;
                        } else if (newLevel < level) { // new parent for other nodes
                            for (int i = 0; i < level - newLevel; i++) {
                                parent = parent.getParent();
                            }
                            level = newLevel;
                        }
                        Category category = new Category(name, parent);
                        parent.getChildren().add(category);
                    } else {
                        Category c = parent.getChildren().get(parent.getChildren().size() - 1);
                        Product currentProduct = extractProduct(row);
                        currentProduct.setCategory(c);
                        productsList.add(currentProduct);
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return productsList;
    }

    private int getCurrentLevel(String cell) {
        // 46 is number for '.' in ASCII table
        return cell.endsWith("0") ? 0 : (int) cell.chars().parallel().filter(c -> ((char) c) == 46).count();
    }

    private boolean isValidCategoryRow(Row row) {
        return row.getSheet().getWorkbook().getFontAt(row.getCell(0).getCellStyle().getFontIndex()).getBold();
    }

    private Product extractProduct(Row row) {
        String name = getStringValueFromCell(row.getCell(1));
        double ediblePart = getDoubleValueFromCell(row.getCell(2));
        double water = getDoubleValueFromCell(row.getCell(4));
        double kilojoules = getDoubleValueFromCell(row.getCell(6));
        double kCal = getDoubleValueFromCell(row.getCell(8));
        double fat = getDoubleValueFromCell(row.getCell(10));
        double satFat = getDoubleValueFromCell(row.getCell(12));
        double transFat = getDoubleValueFromCell(row.getCell(14));
        double muFat = getDoubleValueFromCell(row.getCell(16));
        double puFat = getDoubleValueFromCell(row.getCell(18));
        double omega3 = getDoubleValueFromCell(row.getCell(20));
        double omega6 = getDoubleValueFromCell(row.getCell(22));
        double cholesterol = getDoubleValueFromCell(row.getCell(24));
        double carbo = getDoubleValueFromCell(row.getCell(26));
        double starch = getDoubleValueFromCell(row.getCell(28));
        double monoPlusDi = getDoubleValueFromCell(row.getCell(30));
        double sugar = getDoubleValueFromCell(row.getCell(32));
        double dietaryFibre = getDoubleValueFromCell(row.getCell(34));
        double protein = getDoubleValueFromCell(row.getCell(36));
        double salt = getDoubleValueFromCell(row.getCell(38));
        double alcohol = getDoubleValueFromCell(row.getCell(40));
        double retinol = getDoubleValueFromCell(row.getCell(42));
        double betaCarotene = getDoubleValueFromCell(row.getCell(44));
        double vitaminA = getDoubleValueFromCell(row.getCell(46));
        double vitaminD = getDoubleValueFromCell(row.getCell(48));
        double vitaminE = getDoubleValueFromCell(row.getCell(50));
        double thiamin = getDoubleValueFromCell(row.getCell(52));
        double riboflavin = getDoubleValueFromCell(row.getCell(54));
        double niacin = getDoubleValueFromCell(row.getCell(56));
        double vitaminB6 = getDoubleValueFromCell(row.getCell(58));
        double folate = getDoubleValueFromCell(row.getCell(60));
        double vitaminB12 = getDoubleValueFromCell(row.getCell(62));
        double vitaminC = getDoubleValueFromCell(row.getCell(64));
        double calcium = getDoubleValueFromCell(row.getCell(66));
        double iron = getDoubleValueFromCell(row.getCell(68));
        double sodium = getDoubleValueFromCell(row.getCell(70));
        double potassium = getDoubleValueFromCell(row.getCell(72));
        double magnesium = getDoubleValueFromCell(row.getCell(74));
        double zinc = getDoubleValueFromCell(row.getCell(76));
        double selenium = getDoubleValueFromCell(row.getCell(78));
        double copper = getDoubleValueFromCell(row.getCell(80));
        double phosphorus = getDoubleValueFromCell(row.getCell(82));
        double iodine = getDoubleValueFromCell(row.getCell(84));

        return new Product(name, ediblePart, water, kilojoules, kCal, fat, satFat, transFat, muFat, puFat, omega3, omega6, cholesterol, carbo, starch, monoPlusDi, sugar, dietaryFibre, protein, salt, alcohol, retinol, betaCarotene, vitaminA, vitaminD, vitaminE, thiamin, riboflavin, niacin, vitaminB6, folate, vitaminB12, vitaminC, calcium, iron, sodium, potassium, magnesium, zinc, selenium, copper, phosphorus, iodine);
    }

    private String getStringValueFromCell(Cell cell) {
        String val = "";
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            val = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            val = Double.toString(cell.getNumericCellValue());
        }
        return val;
    }

    private double getDoubleValueFromCell(Cell cell) {
        double val = 0;
        if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
            if ("M".equals(cell.getStringCellValue())) {
                val = 0.0;
            } else {
                val = Double.parseDouble(cell.getStringCellValue());

            }
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            val = cell.getNumericCellValue();
        }
        return val;
    }
}