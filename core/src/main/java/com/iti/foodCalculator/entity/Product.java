package com.iti.foodCalculator.entity;

import org.codehaus.jackson.annotate.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Table(name = "product")
@NamedQueries(value = {
        @NamedQuery(name = "Product.getAllProducts", query = "select p from Product as p"),
})

public class Product implements Serializable {
        public static final String GET_ALL_PRODUCTS = "Product.getAllProducts";

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private int id;
    private String itemName;
    private double kCal;
    private double proteins;
    private double fats;
    private double carbs;
    private String productType;

    @ManyToOne(fetch = FetchType.EAGER, cascade=CascadeType.ALL)
    @JoinColumn(name = "category_id")
    private Category category;

    public Product() {
    }

    public Product(String name, double kcal, double protein, double fat, double carb, String productType, Category category) {
        this.itemName = name;
        this.kCal = kcal;
        this.proteins = protein;
        this.fats = fat;
        this.carbs = carb;
        this.productType = productType;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getkCal() {
        return kCal;
    }

    public void setkCal(double kCal) {
        this.kCal = kCal;
    }

    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (Double.compare(product.carbs, carbs) != 0) return false;
        if (Double.compare(product.fats, fats) != 0) return false;
        if (id != product.id) return false;
        if (Double.compare(product.kCal, kCal) != 0) return false;
        if (Double.compare(product.proteins, proteins) != 0) return false;
        if (!category.equals(product.category)) return false;
        if (!itemName.equals(product.itemName)) return false;
        if (!productType.equals(product.productType)) return false;

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
        result = 31 * result + category.hashCode();
        return result;
    }
}
