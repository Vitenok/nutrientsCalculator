package com.iti.foodCalculator.Entities;

import javax.persistence.*;

/**
 * Created by Vitek on 14/06/2015.
 */
@Entity
@Table(name = "food_composition", schema = "", catalog = "food_composition")
public class FoodCompositionEntity {
    private int id;
    private String itemCode;
    private String itemName;
    private double kCal;
    private double proteins;
    private double fats;
    private double carbs;

    @Id
    @Column(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "itemCode")
    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    @Basic
    @Column(name = "ItemName")
    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Basic
    @Column(name = "kCal")
    public double getkCal() {
        return kCal;
    }

    public void setkCal(double kCal) {
        this.kCal = kCal;
    }

    @Basic
    @Column(name = "Proteins")
    public double getProteins() {
        return proteins;
    }

    public void setProteins(double proteins) {
        this.proteins = proteins;
    }

    @Basic
    @Column(name = "Fats")
    public double getFats() {
        return fats;
    }

    public void setFats(double fats) {
        this.fats = fats;
    }

    @Basic
    @Column(name = "Carbs")
    public double getCarbs() {
        return carbs;
    }

    public void setCarbs(double carbs) {
        this.carbs = carbs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FoodCompositionEntity that = (FoodCompositionEntity) o;

        if (Double.compare(that.carbs, carbs) != 0) return false;
        if (Double.compare(that.fats, fats) != 0) return false;
        if (id != that.id) return false;
        if (Double.compare(that.kCal, kCal) != 0) return false;
        if (Double.compare(that.proteins, proteins) != 0) return false;
        if (itemCode != null ? !itemCode.equals(that.itemCode) : that.itemCode != null) return false;
        if (itemName != null ? !itemName.equals(that.itemName) : that.itemName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id;
        result = 31 * result + (itemCode != null ? itemCode.hashCode() : 0);
        result = 31 * result + (itemName != null ? itemName.hashCode() : 0);
        temp = Double.doubleToLongBits(kCal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(proteins);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(fats);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(carbs);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
