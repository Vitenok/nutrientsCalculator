package com.iti.foodCalculator.entity;

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

    @OneToMany(cascade = CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @JsonManagedReference("user")
//    @JoinColumn(name = "user_id")
    private List<DayFoodPlan> dayFoodPlans;

    public User(String name, String socialNetwork, String token) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.token = token;
    }

    public User() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        if (getId() != null ? !getId().equals(user.getId()) : user.getId() != null) return false;
        if (getName() != null ? !getName().equals(user.getName()) : user.getName() != null) return false;
        if (getSocialNetwork() != user.getSocialNetwork()) return false;
        return !(getToken() != null ? !getToken().equals(user.getToken()) : user.getToken() != null);

    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getSocialNetwork() != null ? getSocialNetwork().hashCode() : 0);
        result = 31 * result + (getToken() != null ? getToken().hashCode() : 0);
        return result;
    }
}
