package com.iti.foodCalculator.entity;

import org.codehaus.jackson.annotate.JsonBackReference;
import org.codehaus.jackson.annotate.JsonManagedReference;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Category implements Serializable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;

    private String name;

    @ManyToOne(cascade=CascadeType.ALL)
    @JsonBackReference
    private Category parent;

    @OneToMany(mappedBy="parent")
    @JsonManagedReference
    private List<Category> children;


    @OneToMany
    @JoinColumn(name="id")
    private List<Product> products;

    public Category() {
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }

    public List<Product> getProduct() {
        return products;
    }

    public void setProduct(List<Product> products) {
        this.products = products;
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

    public List<Category> getChildren() {
        if (children == null) {
            children = new ArrayList<Category>();
        }
        return children;
    }

    public void setChildren(List<Category> children) {
        this.children = children;
    }

    public Category getParent() {
        return parent;
    }

    public void setParent(Category parent) {
        this.parent = parent;
    }

//    public List<Product> getProducts() {
//        if (product == null) {
//            product = new ArrayList<>();
//        }
//        return product;
//    }
//
//    public void setProducts(List<Product> product) {
//        this.product = product == null ? new ArrayList<Product>() : product;
//    }


//    public Category getCategories() {
//        return categories;
//    }
//
//    public void setCategories(Category categories) {
//        this.categories = categories;
//    }
}
