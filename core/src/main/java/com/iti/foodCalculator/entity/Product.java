package com.iti.foodCalculator.entity;

import java.io.Serializable;


//@Entity
//@Table
//@NamedQueries(value = {
//        @NamedQuery(name = "Product.getAllProducts", query = "select p from Product as p"),
//})

public class Product implements Serializable {
    private int id;
    private String itemName;
    private double kCal;
    private double proteins;
    private double fats;
    private double carbs;
    private String productType;


//    public static final String GET_ALL_PRODUCTS = "Product.getAllProducts";

    public Product() {
    }

    public Product(String name, double kcal, double protein, double fat, double carb, String productType) {
        this.itemName = name;
        this.kCal = kcal;
        this.proteins = protein;
        this.fats = fat;
        this.carbs = carb;
        this.productType = productType;
    }

//    @Id
//    @Column(name = "id")
//    @GeneratedValue(strategy = GenerationType.AUTO)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


//    @Basic
//    @Column(name = "ItemName")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

//    @Basic
//    @Column(name = "kCal")
    public double getkCal() {
        return kCal;
    }

    public void setkCal(double kCal) {
        this.kCal = kCal;
    }
//
//    @Basic
//    @Column(name = "Proteins")
    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

//    @Basic
//    @Column(name = "Fats")
    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

//    @Basic
//    @Column(name = "Carbs")
    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

//    @Basic
//    @Column(name = "productType")
    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        if (Double.compare(product.carbs, carbs) != 0) return false;
        if (Double.compare(product.fats, fats) != 0) return false;
        if (id != product.id) return false;
        if (Double.compare(product.kCal, kCal) != 0) return false;
        if (Double.compare(product.proteins, proteins) != 0) return false;
        if (!itemName.equals(product.itemName)) return false;
        if (productType != product.productType) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + itemName.hashCode();
        temp = Double.doubleToLongBits(kCal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(proteins);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fats);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(carbs);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + productType.hashCode();
        return result;
    }


}
