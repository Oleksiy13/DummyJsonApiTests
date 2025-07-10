package com.dummyjson.api.tests.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true) // игнорим ненужные поля
@JsonInclude(JsonInclude.Include.NON_NULL) //игнорим поля NULL
public class Product {
    private Integer id; //при создании товара примитивный int не может быть null перед присвоением ID
    private String title;
    private String description;
    private float price;
    private int stock;
    private String category;
    private String brand;
    private Boolean isDeleted;
    private String deletedOn;

    public Product() {

    }


    public Product(String title, String description, float price, int stock, String category, String brand) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
    }

    @JsonCreator
    public Product(
            @JsonProperty("id") Integer id,
            @JsonProperty("title") String title,
            @JsonProperty("description") String description,
            @JsonProperty("price") float price,
            @JsonProperty("stock") int stock,
            @JsonProperty("category") String category,
            @JsonProperty("brand") String brand,
            @JsonProperty("isDeleted") Boolean isDeleted,
            @JsonProperty("deletedOn") String deletedOn)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.category = category;
        this.brand = brand;
        this.isDeleted = isDeleted;
        this.deletedOn = deletedOn;
    }


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public float getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public String getDeletedOn() {
        return deletedOn;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setIsDeleted(Boolean deleted) {
        isDeleted = deleted;
    }

    public void setDeletedOn(String deletedOn) {
        this.deletedOn = deletedOn;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", isDeleted=" + isDeleted +
                ", deletedOn='" + deletedOn + '\'' +
                '}';
    }
}
