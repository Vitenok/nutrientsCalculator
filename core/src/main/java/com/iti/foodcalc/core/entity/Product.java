package com.iti.foodcalc.core.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;


@Entity
public class Product implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private double protein;
    private double carbo;
    private double fat;
    private double kCal;

    private TYPE type = TYPE.PRODUCT;

    private int serving;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JsonBackReference("category-products")
    private Category category;

    @ManyToOne
    @JsonBackReference("user")
    private User user;

    @JsonIgnore
    private double ediblePart;
    @JsonIgnore
    private double water;
    @JsonIgnore
    private double kilojoules;
    @JsonIgnore
    private double satFat;
    @JsonIgnore
    private double transFat;
    @JsonIgnore
    private double muFat;
    @JsonIgnore
    private double puFat;
    @JsonIgnore
    private double omega3;
    @JsonIgnore
    private double omega6;
    @JsonIgnore
    private double cholesterol;
    @JsonIgnore
    private double starch;
    @JsonIgnore
    private double monoPlusDi;
    @JsonIgnore
    private double sugar;
    @JsonIgnore
    private double dietaryFibre;
    @JsonIgnore
    private double salt;
    @JsonIgnore
    private double alcohol;
    @JsonIgnore
    private double retinol;
    @JsonIgnore
    private double betaCarotene;
    @JsonIgnore
    private double vitaminA;
    @JsonIgnore
    private double vitaminD;
    @JsonIgnore
    private double vitaminE;
    @JsonIgnore
    private double thiamin;
    @JsonIgnore
    private double riboflavin;
    @JsonIgnore
    private double niacin;
    @JsonIgnore
    private double vitaminB6;
    @JsonIgnore
    private double folate;
    @JsonIgnore
    private double vitaminB12;
    @JsonIgnore
    private double vitaminC;
    @JsonIgnore
    private double calcium;
    @JsonIgnore
    private double iron;
    @JsonIgnore
    private double sodium;
    @JsonIgnore
    private double potassium;
    @JsonIgnore
    private double magnesium;
    @JsonIgnore
    private double zinc;
    @JsonIgnore
    private double selenium;
    @JsonIgnore
    private double copper;
    @JsonIgnore
    private double phosphorus;
    @JsonIgnore
    private double iodine;

    public enum TYPE {
        PRODUCT(0), SUPPLEMENT(1);

        private final int value;
        TYPE(int id) { this.value = id; }
        public int getValue() { return value; }
    }

    public Product() {
    }

    public Product(String name, double kcal, double protein, double fat, double carb) {
        this.name = name;
        this.kCal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbo = carb;
    }

    public Product(String name, int id, double kcal, double protein, double fat, double carb) {
        this.name = name;
        this.id = id;
        this.kCal = kcal;
        this.protein = protein;
        this.fat = fat;
        this.carbo = carb;
    }

    public Product(double protein, double carbo, double fat) {
        this.protein = protein;
        this.carbo = carbo;
        this.fat = fat;
    }

    public Product(String name, double ediblePart, double water, double kilojoules, double kCal, double fat, double satFat, double transFat, double muFat, double puFat, double omega3, double omega6, double cholesterol, double carbo, double starch, double monoPlusDi, double sugar, double dietaryFibre, double protein, double salt, double alcohol, double retinol, double betaCarotene, double vitaminA, double vitaminD, double vitaminE, double thiamin, double riboflavin, double niacin, double vitaminB6, double folate, double vitaminB12, double vitaminC, double calcium, double iron, double sodium, double potassium, double magnesium, double zinc, double selenium, double copper, double phosphorus, double iodine) {
        this.name = name;
        this.ediblePart = ediblePart;
        this.water = water;
        this.kilojoules = kilojoules;
        this.kCal = kCal;
        this.fat = fat;
        this.satFat = satFat;
        this.transFat = transFat;
        this.muFat = muFat;
        this.puFat = puFat;
        this.omega3 = omega3;
        this.omega6 = omega6;
        this.cholesterol = cholesterol;
        this.carbo = carbo;
        this.starch = starch;
        this.monoPlusDi = monoPlusDi;
        this.sugar = sugar;
        this.dietaryFibre = dietaryFibre;
        this.protein = protein;
        this.salt = salt;
        this.alcohol = alcohol;
        this.retinol = retinol;
        this.betaCarotene = betaCarotene;
        this.vitaminA = vitaminA;
        this.vitaminD = vitaminD;
        this.vitaminE = vitaminE;
        this.thiamin = thiamin;
        this.riboflavin = riboflavin;
        this.niacin = niacin;
        this.vitaminB6 = vitaminB6;
        this.folate = folate;
        this.vitaminB12 = vitaminB12;
        this.vitaminC = vitaminC;
        this.calcium = calcium;
        this.iron = iron;
        this.sodium = sodium;
        this.potassium = potassium;
        this.magnesium = magnesium;
        this.zinc = zinc;
        this.selenium = selenium;
        this.copper = copper;
        this.phosphorus = phosphorus;
        this.iodine = iodine;
    }

    public Product(Product product) {
        this.kCal = product.kCal;
        this.protein = product.protein;
        this.carbo = product.carbo;
        this.fat = product.fat;
        this.name = product.name;
        this.category = product.category;
    }

    public Product add(Product another) {
        this.protein += another.protein;
        this.carbo += another.carbo;
        this.fat += another.fat;
        this.kCal += another.kCal;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getEdiblePart() {
        return ediblePart;
    }

    public void setEdiblePart(double ediblePart) {
        this.ediblePart = ediblePart;
    }

    public double getWater() {
        return water;
    }

    public void setWater(double water) {
        this.water = water;
    }

    public double getKilojoules() {
        return kilojoules;
    }

    public void setKilojoules(double kilojoules) {
        this.kilojoules = kilojoules;
    }

    public double getkCal() {
        return kCal;
    }

    public void setkCal(double kCal) {
        this.kCal = kCal;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public double getSatFat() {
        return satFat;
    }

    public void setSatFat(double satFat) {
        this.satFat = satFat;
    }

    public double getTransFat() {
        return transFat;
    }

    public void setTransFat(double transFat) {
        this.transFat = transFat;
    }

    public double getMuFat() {
        return muFat;
    }

    public void setMuFat(double muFat) {
        this.muFat = muFat;
    }

    public double getPuFat() {
        return puFat;
    }

    public void setPuFat(double puFat) {
        this.puFat = puFat;
    }

    public double getOmega3() {
        return omega3;
    }

    public void setOmega3(double omega3) {
        this.omega3 = omega3;
    }

    public double getOmega6() {
        return omega6;
    }

    public void setOmega6(double omega6) {
        this.omega6 = omega6;
    }

    public double getCholesterol() {
        return cholesterol;
    }

    public void setCholesterol(double cholesterol) {
        this.cholesterol = cholesterol;
    }

    public double getCarbo() {
        return carbo;
    }

    public void setCarbo(double carbo) {
        this.carbo = carbo;
    }

    public double getStarch() {
        return starch;
    }

    public void setStarch(double starch) {
        this.starch = starch;
    }

    public double getMonoPlusDi() {
        return monoPlusDi;
    }

    public void setMonoPlusDi(double monoPlusDi) {
        this.monoPlusDi = monoPlusDi;
    }

    public double getSugar() {
        return sugar;
    }

    public void setSugar(double sugar) {
        this.sugar = sugar;
    }

    public double getDietaryFibre() {
        return dietaryFibre;
    }

    public void setDietaryFibre(double dietaryFibre) {
        this.dietaryFibre = dietaryFibre;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getSalt() {
        return salt;
    }

    public void setSalt(double salt) {
        this.salt = salt;
    }

    public double getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(double alcohol) {
        this.alcohol = alcohol;
    }

    public double getRetinol() {
        return retinol;
    }

    public void setRetinol(double retinol) {
        this.retinol = retinol;
    }

    public double getBetaCarotene() {
        return betaCarotene;
    }

    public void setBetaCarotene(double betaCarotene) {
        this.betaCarotene = betaCarotene;
    }

    public double getVitaminA() {
        return vitaminA;
    }

    public void setVitaminA(double vitaminA) {
        this.vitaminA = vitaminA;
    }

    public double getVitaminD() {
        return vitaminD;
    }

    public void setVitaminD(double vitaminD) {
        this.vitaminD = vitaminD;
    }

    public double getVitaminE() {
        return vitaminE;
    }

    public void setVitaminE(double vitaminE) {
        this.vitaminE = vitaminE;
    }

    public double getThiamin() {
        return thiamin;
    }

    public void setThiamin(double thiamin) {
        this.thiamin = thiamin;
    }

    public double getRiboflavin() {
        return riboflavin;
    }

    public void setRiboflavin(double riboflavin) {
        this.riboflavin = riboflavin;
    }

    public double getNiacin() {
        return niacin;
    }

    public void setNiacin(double niacin) {
        this.niacin = niacin;
    }

    public double getVitaminB6() {
        return vitaminB6;
    }

    public void setVitaminB6(double vitaminB6) {
        this.vitaminB6 = vitaminB6;
    }

    public double getFolate() {
        return folate;
    }

    public void setFolate(double folate) {
        this.folate = folate;
    }

    public double getVitaminB12() {
        return vitaminB12;
    }

    public void setVitaminB12(double vitaminB12) {
        this.vitaminB12 = vitaminB12;
    }

    public double getVitaminC() {
        return vitaminC;
    }

    public void setVitaminC(double vitaminC) {
        this.vitaminC = vitaminC;
    }

    public double getCalcium() {
        return calcium;
    }

    public void setCalcium(double calcium) {
        this.calcium = calcium;
    }

    public double getIron() {
        return iron;
    }

    public void setIron(double iron) {
        this.iron = iron;
    }

    public double getSodium() {
        return sodium;
    }

    public void setSodium(double sodium) {
        this.sodium = sodium;
    }

    public double getPotassium() {
        return potassium;
    }

    public void setPotassium(double potassium) {
        this.potassium = potassium;
    }

    public double getMagnesium() {
        return magnesium;
    }

    public void setMagnesium(double magnesium) {
        this.magnesium = magnesium;
    }

    public double getZinc() {
        return zinc;
    }

    public void setZinc(double zinc) {
        this.zinc = zinc;
    }

    public double getSelenium() {
        return selenium;
    }

    public void setSelenium(double selenium) {
        this.selenium = selenium;
    }

    public double getCopper() {
        return copper;
    }

    public void setCopper(double copper) {
        this.copper = copper;
    }

    public double getPhosphorus() {
        return phosphorus;
    }

    public void setPhosphorus(double phosphorus) {
        this.phosphorus = phosphorus;
    }

    public double getIodine() {
        return iodine;
    }

    public void setIodine(double iodine) {
        this.iodine = iodine;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TYPE getType() {
        return type;
    }

    public void setType(TYPE type) {
        this.type = type;
    }

    public int getServing() {
        return serving;
    }

    public void setServing(int serving) {
        this.serving = serving;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;

        Product product = (Product) o;

        if (id != product.id) return false;
        if (Double.compare(product.protein, protein) != 0) return false;
        if (Double.compare(product.carbo, carbo) != 0) return false;
        if (Double.compare(product.fat, fat) != 0) return false;
        if (Double.compare(product.kCal, kCal) != 0) return false;
        if (type != product.type) return false;
        return name.equals(product.name);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + name.hashCode();
        temp = Double.doubleToLongBits(protein);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(carbo);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(kCal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + type.getValue();
        return result;
    }

    @Override
    public String toString() {
        return "Product{" +
                "kCal=" + kCal +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", protein=" + protein +
                ", carbo=" + carbo +
                ", fat=" + fat +
                ", type=" + type +
                '}';
    }
}
