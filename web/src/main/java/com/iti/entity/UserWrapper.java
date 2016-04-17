package com.iti.entity;

public class UserWrapper {
    private String name;
    private String socialNetwork;
    private String token;

    public UserWrapper(){}

    public UserWrapper(String name, String socialNetwork, String token) {
        this.name = name;
        this.socialNetwork = socialNetwork;
        this.token = token;
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
}