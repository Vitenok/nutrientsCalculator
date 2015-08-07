package com.iti.foodCalculator.entity;

import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.List;

@XmlRootElement
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class CalculationInputDomainModel implements Serializable{

    private List<Product> products;
    private List<SupplementItem> supplementItems;
    private DailyMacroelementsInput dailyMacroelementsInput;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<SupplementItem> getSupplementItems() {
        return supplementItems;
    }

    public void setSupplementItems(List<SupplementItem> supplementItems) {
        this.supplementItems = supplementItems;
    }

    public DailyMacroelementsInput getDailyMacroelementsInput() {
        return dailyMacroelementsInput;
    }

    public void setDailyMacroelementsInput(DailyMacroelementsInput dailyMacroelementsInput) {
        this.dailyMacroelementsInput = dailyMacroelementsInput;
    }
}
