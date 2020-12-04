package com.company;

public class Product {

    private String productName;
    private double sellPrice;
    private double buyPrice;
    private int Stock;
    private Warehouse warehouse;

    //todo: add other attributes
    public Product(String productName){

        this.productName=productName;
    }


    //todo: write setters and getters
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductName(){
        return productName;
    }


}
