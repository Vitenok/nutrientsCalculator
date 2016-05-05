package com.iti.foodcalc.core.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String socialNetwork;
    private String socialNetworkId;

    private SEX sex;
    private double height;
    private double goal;
    private int age;
    private double weight;
    private double activityLevel;
    private int totalCalories;
    private int proteinPercent;
    private int fatPercent;
    private int carbohydratePercent;

    public enum SEX {MALE, FEMALE};

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @OrderBy("date DESC")
    @JsonIgnore
    private List<DayFoodPlan> dayFoodPlans;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference("user")
    private List<Product> products;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user", orphanRemoval = true)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference("user")
    private List<UserProductServing> servings;

    public User(String name, String socialNetwork, String socialNetworkId, SEX sex, double height, int goal, int age, double weight, int activityLevel, int totalCalories, int proteinPercent, int fatPercent, int carbohydratePercent, List<DayFoodPlan> dayFoodPlans) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.socialNetworkId = socialNetworkId;
        this.sex = sex;
        this.height = height;
        this.goal = goal;
        this.age = age;
        this.weight = weight;
        this.activityLevel = activityLevel;
        this.totalCalories = totalCalories;
        this.proteinPercent = proteinPercent;
        this.fatPercent = fatPercent;
        this.carbohydratePercent = carbohydratePercent;
        this.dayFoodPlans = dayFoodPlans;
    }

    public User(String name, String socialNetwork, String socialNetworkId) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.socialNetworkId = socialNetworkId;
    }

    public User() {
    }

    public void addDayFoodPlan(DayFoodPlan dayFoodPlan) {
        getDayFoodPlans().add(dayFoodPlan);
        dayFoodPlan.setUser(this);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSocialNetwork() {
        return socialNetwork;
    }

    public void setSocialNetwork(String socialNetwork) {
        this.socialNetwork = socialNetwork;
    }

    public String getSocialNetworkId() {
        return socialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        this.socialNetworkId = socialNetworkId;
    }

    public List<DayFoodPlan> getDayFoodPlans() {
        if (dayFoodPlans == null) {
            dayFoodPlans = new ArrayList<>();
        }
        return dayFoodPlans;
    }

    public void setDayFoodPlans(List<DayFoodPlan> dayFoodPlan) {
        this.dayFoodPlans = dayFoodPlan;
    }

    public SEX getSex() {
        return sex;
    }

    public void setSex(SEX sex) {
        this.sex = sex;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getGoal() {
        return goal;
    }

    public void setGoal(double goal) {
        this.goal = goal;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(double activityLevel) {
        this.activityLevel = activityLevel;
    }

    public int getTotalCalories() {
        return totalCalories;
    }

    public void setTotalCalories(int totalCalories) {
        this.totalCalories = totalCalories;
    }

    public int getProteinPercent() {
        return proteinPercent;
    }

    public void setProteinPercent(int proteinPercent) {
        this.proteinPercent = proteinPercent;
    }

    public int getFatPercent() {
        return fatPercent;
    }

    public void setFatPercent(int fatPercent) {
        this.fatPercent = fatPercent;
    }

    public int getCarbohydratePercent() {
        return carbohydratePercent;
    }

    public void setCarbohydratePercent(int carbohydratePercent) {
        this.carbohydratePercent = carbohydratePercent;
    }

    public List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
        }
        return products;
    }

    public void addProduct(Product product) {
        product.setUser(this);
        getProducts().add(product);
    }

    public void addProducts(List<Product> products) {
        products.stream().forEach(this::addProduct);
    }

    public void setProducts(List<Product> products) {
        getProducts().clear();
        addProducts(products);
    }

    public List<UserProductServing> getServings() {
        if (servings == null) {
            servings = new ArrayList<>();
        }
        return servings;
    }

    public void addServing(UserProductServing serving) {
        serving.setUser(this);
        getServings().add(serving);
    }

    public void addServings(List<UserProductServing> servings) {
        servings.stream().forEach(this::addServing);
    }

    public void setServings(List<UserProductServing> servings) {
        getServings().clear();
        addServings(servings);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;
        if (!name.equals(user.name)) return false;
        if (!socialNetwork.equals(user.socialNetwork)) return false;
        return socialNetworkId.equals(user.socialNetworkId);

    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + socialNetwork.hashCode();
        result = 31 * result + socialNetworkId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", socialNetwork='" + socialNetwork + '\'' +
                ", socialNetworkId='" + socialNetworkId + '\'' +
                ", sex=" + sex +
                ", height=" + height +
                ", goal=" + goal +
                ", age=" + age +
                ", weight=" + weight +
                ", activityLevel=" + activityLevel +
                ", totalCalories=" + totalCalories +
                ", proteinPercent=" + proteinPercent +
                ", fatPercent=" + fatPercent +
                ", carbohydratePercent=" + carbohydratePercent +
                '}';
    }
}