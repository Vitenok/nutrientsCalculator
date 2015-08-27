package com.iti.foodCalculator.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//@Entity
//@Table
public class Category implements Serializable {
//    @Id
//    @Column
//    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

//    @Column(nullable = false)
    private String name;

//    @OneToMany
//    @JoinColumn(name = "parent_id")
    private List<Category> children;

//    @ManyToOne(fetch=FetchType.LAZY)
//    @JoinColumn(name = "parent_id")
    private Category parent;

//    @OneToMany
//    @JoinColumn(name = "parent_id")
    private List<Product> product;

    public Category() {
    }

    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
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

    public List<Product> getProducts() {
        if (product == null) {
            product = new ArrayList<>();
        }
        return product;
    }

    public void setProducts(List<Product> product) {
        this.product = product == null ? new ArrayList<Product>() : product;
    }

    @ManyToOne(optional = false)
    private Category categories;

    public Category getCategories() {
        return categories;
    }

    public void setCategories(Category categories) {
        this.categories = categories;
    }
}
