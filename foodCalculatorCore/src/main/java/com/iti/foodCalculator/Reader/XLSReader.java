package com.iti.foodCalculator.Reader;

import com.iti.foodCalculator.Entities.FoodItem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XLSReader {
    public static void main(String[] args) {
        XLSReader reader = new XLSReader();
        reader.readXlsFileToList();

    }

    public List<FoodItem> readXlsFileToList() {
        List<FoodItem> foodItemsList = new ArrayList<FoodItem>();
        try {
            FileInputStream file = new FileInputStream(new File("E:\\eclipse\\workspace\\nutrientsCalculator\\foodCalculatorCore\\src\\main\\resources\\The+Norwegian+Food+Compostion+Table+2014.ods.xlsx"));

            //Get the workbook instance for XLS file
            XSSFWorkbook workbook = new XSSFWorkbook(file);

            //Get first sheet from the workbook
            XSSFSheet sheet = workbook.getSheetAt(0);

            //Iterate through each rows from first sheet
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row.getRowNum() >= 7) {
                    if (row.getLastCellNum() <= 2 || isInvalidStyle(row.getCell(0))) {
                        rowIterator.remove();
                    } else {
                        String id = getStringValueFromCell(row.getCell(0));
                        String name = getStringValueFromCell(row.getCell(1));
                        Double kcal = getDoubleValueFromCell(row.getCell(8));
                        Double protein = getDoubleValueFromCell(row.getCell(32));
                        Double fat = getDoubleValueFromCell(row.getCell(10));
                        Double carb = getDoubleValueFromCell(row.getCell(22));

                        FoodItem foodItem = new FoodItem(id, name, kcal, protein, fat, carb);

                        foodItemsList.add(foodItem);
                    }
                }
            }
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(foodItemsList.size());
        return foodItemsList;
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

    private boolean isInvalidStyle(Cell cell) {
        short fontIndex = cell.getCellStyle().getFontIndex();
        if (fontIndex == 5 || fontIndex == 0 || fontIndex == 7) {
            return true;
        } else {
            return false;
        }
    }


}
