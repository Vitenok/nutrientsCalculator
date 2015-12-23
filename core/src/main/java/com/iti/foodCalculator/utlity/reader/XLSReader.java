package com.iti.foodCalculator.utlity.reader;

import com.iti.foodCalculator.entity.Category;
import com.iti.foodCalculator.entity.Product;
import com.iti.foodCalculator.entity.ProductType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class XLSReader {
    public static void main(String[] args) {
        XLSReader reader = new XLSReader();
        reader.readXlsFileToList().size();
    }

    public List<Product> readXlsFileToList() {
        List<Product> productsList = new ArrayList<Product>();
        Set<Category> categories = new HashSet<Category>();
        try {
//            String fileName = getClass().getClassLoader().getResource("The+Norwegian+Food+Compostion+Table+2014.ods.xlsx").getFile();
//            FileInputStream file = new FileInputStream(new File(fileName));
            //TODO:
            FileInputStream file = new FileInputStream(new File("E:\\eclipse\\workspace\\nutrientsCalculator\\core\\src\\main\\resources\\The+Norwegian+Food+Compostion+Table+2014.ods.xlsx"));

            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            Category root = new Category("Food", null);
            Category parent = root;
            int level = 0;
            int newLevel;

            for (Iterator<Row> rowIterator = sheet.rowIterator(); rowIterator.hasNext(); ) {
                Row row = rowIterator.next();

                if (row.getRowNum() > 4 && row.getCell(1).toString().length() != 0) {
                    if (isValidCategoryRow(row)) { // valid = text is bold
                        row.getCell(0).setCellType(Cell.CELL_TYPE_STRING); // because some of them are in numeric format

                        newLevel = getCurrentLevel(row.getCell(0).getStringCellValue());
                        String name = row.getCell(1).getStringCellValue();

                        //System.out.println(new String(new char[newLevel]).replace("\0", "\t") + name);
                        if (newLevel > level) { // child
                            parent = parent.getChildren().get(parent.getChildren().size() - 1);
                            level = newLevel;
                        } else if (newLevel < level) { // new parent for other nodes
                            for (int i = 0; i < level - newLevel; i++) {
                                parent = parent.getParent();
                            }
                            level = newLevel;
                        }
                        parent.getChildren().add(new Category(name, parent));
                    } else {
                        Category c = parent.getChildren().get(parent.getChildren().size() - 1);
//                        c.getProducts().add(extractProduct(row));
                        Product currentProduct = extractProduct(row);
                        currentProduct.setCategory(c);
//                        currentProduct.setCategoryName(c.getName());
                        productsList.add(currentProduct);
                        categories.add(c);
                        //root.getProducts().add(extractProduct(row));
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
//        String id = getStringValueFromCell(row.getCell(0));
        String name = getStringValueFromCell(row.getCell(1));
        Double kcal = getDoubleValueFromCell(row.getCell(8));
        Double protein = getDoubleValueFromCell(row.getCell(32));
        Double fat = getDoubleValueFromCell(row.getCell(10));
        Double carb = getDoubleValueFromCell(row.getCell(22));
        String productType = String.valueOf(ProductType.NON_SUPPLEMENT);
        //TODO
        Category category = null;

        return new Product(name, kcal, protein, fat, carb, productType, category);
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

    private boolean isValidStyle(Cell cell) {
        short fontIndex = cell.getCellStyle().getFontIndex();
        return !(fontIndex == 5 || fontIndex == 0 || fontIndex == 7);
    }
}