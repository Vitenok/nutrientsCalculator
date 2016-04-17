package com.iti.foodCalculator.entity;

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
    private String token;

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
//    @JsonManagedReference("user")
    @JsonIgnore
    private List<DayFoodPlan> dayFoodPlans;

    public User(String name, String socialNetwork, String token, SEX sex, double height, int goal, int age, double weight, int activityLevel, int totalCalories, int proteinPercent, int fatPercent, int carbohydratePercent, List<DayFoodPlan> dayFoodPlans) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.token = token;
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

    public User(String name, String socialNetwork, String token) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.token = token;
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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (Double.compare(user.height, height) != 0) return false;
        if (age != user.age) return false;
        if (Double.compare(user.weight, weight) != 0) return false;
        if (totalCalories != user.totalCalories) return false;
        if (proteinPercent != user.proteinPercent) return false;
        if (fatPercent != user.fatPercent) return false;
        if (carbohydratePercent != user.carbohydratePercent) return false;
        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (name != null ? !name.equals(user.name) : user.name != null) return false;
        if (socialNetwork != null ? !socialNetwork.equals(user.socialNetwork) : user.socialNetwork != null)
            return false;
        if (token != null ? !token.equals(user.token) : user.token != null) return false;
        if (sex != user.sex) return false;
        if (goal != user.goal) return false;
        if (activityLevel != user.activityLevel) return false;
        return !(dayFoodPlans != null ? !dayFoodPlans.equals(user.dayFoodPlans) : user.dayFoodPlans != null);

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (socialNetwork != null ? socialNetwork.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (sex != null ? sex.hashCode() : 0);
        temp = Double.doubleToLongBits(height);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(goal);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + age;
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(activityLevel);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + totalCalories;
        result = 31 * result + proteinPercent;
        result = 31 * result + fatPercent;
        result = 31 * result + carbohydratePercent;
        result = 31 * result + (dayFoodPlans != null ? dayFoodPlans.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", socialNetwork='" + socialNetwork + '\'' +
                ", token='" + token + '\'' +
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