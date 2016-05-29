package com.iti.foodcalc.core.service.helper;

import com.iti.foodcalc.core.dao.ProductsDAO;
import com.iti.foodcalc.core.dao.UserDAO;
import com.iti.foodcalc.core.entity.Product;
import com.iti.foodcalc.core.entity.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductsSeparator {

    private static final Logger LOG = LogManager.getLogger(ProductsSeparator.class);

    @Autowired
    UserDAO userDAO;

    @Autowired
    ProductsDAO productsDAO;

    @Value("#{'${products.fruits}'.split(',')}")
    private List<Integer> fruitIds;
    @Value("#{'${products.vegetables}'.split(',')}")
    private List<Integer> vegetablesIds;
    @Value("${products.vegetables.gramsPerDay}")
    private int vegetablesWeightPerDay;
    @Value("${products.fruits.gramsPerDay}")
    private int fruitsWeightPerDay;
    @Value("${products.vegetables.maxCalories}")
    private int vegetablesMaxCalories;
    @Value("${products.fruits.maxCalories}")
    private int fruitsMaxCalories;

    @Value("${calculator.protein.caloriesInGram}")
    private int caloriesInProtein;
    @Value("${calculator.fat.caloriesInGram}")
    private int caloriesInFat;
    @Value("${calculator.carbohydrate.caloriesInGram}")
    private int caloriesInCarbohydrate;

    private User user;
    private List<List<Product>> products;
    private LocalDateTime date;

    //Calculated fields
    private List<Product> flatProducts;
    private List<Product> uniqueProducts;
    private List<Product> supplements;
    private List<Product> fruits;
    private List<Product> vegetables;
    private List<Product> productsToCalculate;
    private Product removeFromConstraints;

    private Map<Integer, Integer> frequencies;
    private Map<Integer, Integer> productsMap;



    public void init(User user, List<List<Product>> products, LocalDateTime date) {
        this.user = user;
        this.products = products;
        this.date = date;

        init();
    }

    private void init() {
        productsMap = new HashMap<>();
        flatProducts = products.stream().flatMap(Collection::stream).collect(Collectors.toList());
        LOG.debug("Came " + flatProducts.size() + " products");
        uniqueProducts = flatProducts.stream().distinct().collect(Collectors.toList());
        LOG.debug("Unique: " + uniqueProducts.size());
        supplements = uniqueProducts.stream().filter(p->Product.TYPE.SUPPLEMENT.equals(p.getType())).collect(Collectors.toList());
        LOG.debug("Including: " + supplements.size() + " supplements");
        supplements.stream().forEach(s-> System.out.print(s.getName() + ","));

        frequencies = new HashMap<>();
        for (Product p : uniqueProducts) {
            frequencies.put(p.getId(), Collections.frequency(flatProducts, p));
        }

        List<Integer> nonSupplementsIds = getUniqueProducts()
                .stream()
                .filter(p -> Product.TYPE.PRODUCT.equals(p.getType()))
                .mapToInt(Product::getId)
                .boxed()
                .collect(Collectors.toList());
        List<Product> nonSupplementsProducts = nonSupplementsIds.isEmpty() ? Collections.emptyList() : productsDAO.findByIds(nonSupplementsIds);

        fruits = nonSupplementsProducts
                .stream()
                .filter(p->fruitIds.contains(p.getCategory().getId()) && p.getkCal() < fruitsMaxCalories)
                .collect(Collectors.toList());
        nonSupplementsProducts.removeAll(fruits);
        LOG.debug("Including: " + fruits.size() + " fruits with kCal<" + fruitsMaxCalories);
        fruits.stream().forEach(s-> System.out.print(s.getName() + "(" + s.getkCal()+" kCal), "));

        vegetables = nonSupplementsProducts
                .stream()
                .filter(p->vegetablesIds.contains(p.getCategory().getId()) && p.getkCal() < vegetablesMaxCalories)
                .collect(Collectors.toList());
        nonSupplementsProducts.removeAll(vegetables);
        LOG.debug("Including: " + vegetables.size() + " vegetables with kCal<" + vegetablesMaxCalories);
        vegetables.stream().forEach(s-> System.out.print(s.getName() + "(" + s.getkCal()+" kCal), "));

        productsToCalculate = nonSupplementsProducts;
        LOG.debug("Products to go for calculation: " + productsToCalculate.size());
        productsToCalculate.stream().forEach(s-> System.out.print(s.getName() + "(" + s.getkCal()+" kCal), "));

        LOG.debug("Map of products with weights, calculated by proportion:");
        removeFromConstraints = aggregateProductAndUpdateMap(supplements, 0);

        double fruitsCount = fruits
                .stream()
                .mapToInt(pr -> frequencies.get(pr.getId()))
                .sum();
        removeFromConstraints.add(aggregateProductAndUpdateMap(fruits, (int)Math.round(fruitsWeightPerDay / fruitsCount)));

        double vegetablesCount = vegetables
                .stream()
                .mapToInt(pr -> frequencies.get(pr.getId()))
                .sum();
        removeFromConstraints.add(aggregateProductAndUpdateMap(vegetables, (int)Math.round(vegetablesWeightPerDay / vegetablesCount)));

    }

    private Product aggregateProductAndUpdateMap(List<Product> products, int weight) {
        double p = 0;
        double c = 0;
        double f = 0;
        for (Product product : products) {
            int frequency = frequencies.get(product.getId());
            int usedWeight = weight==0?product.getServing():weight;
            p += product.getProtein()*frequency*usedWeight/100;
            c += product.getCarbo()*frequency*usedWeight/100;
            f += product.getFat()*frequency*usedWeight/100;
            productsMap.put(product.getId(), usedWeight);
            LOG.debug(product.getName() + " : " + (frequency*usedWeight));
        }
        Product product = new Product(p, c, f);
        product.setkCal(p*caloriesInProtein+c*caloriesInCarbohydrate+f*caloriesInFat);
        return product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<List<Product>> getProducts() {
        return products;
    }

    public void setProducts(List<List<Product>> products) {
        this.products = products;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public List<Product> getFlatProducts() {
        return flatProducts;
    }

    public List<Product> getUniqueProducts() {
        return uniqueProducts;
    }

    public List<Product> getSupplements() {
        return supplements;
    }

    public List<Product> getFruits() {
        return fruits;
    }

    public List<Product> getVegetables() {
        return vegetables;
    }

    public List<Product> getProductsToCalculate() {
        return productsToCalculate;
    }

    public Map<Integer, Integer> getFrequencies() {
        return frequencies;
    }

    public Product getRemoveFromConstraints() {
        return removeFromConstraints;
    }

    public Map<Integer, Integer> getProductsMap() {
        return productsMap;
    }
}
