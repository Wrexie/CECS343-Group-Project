package com.company;

public class Product {

    private int productID;
    private String productName;
    private double sellPrice;
    private double buyPrice;
    private int stock;
    private Warehouse warehouse;

    //todo: add other attributes
    public Product(String productName, double sellPrice, double buyPrice, int stock, Warehouse warehouse){
        this.productName=productName;
        this.sellPrice=sellPrice;
        this.buyPrice=buyPrice;
        this.stock=stock;
        this.warehouse=warehouse;
    }

    public Product(int productID, String productName, double sellPrice, double buyPrice, int stock, Warehouse warehouse) {
        this.productID = productID;
        this.productName = productName;
        this.sellPrice = sellPrice;
        this.buyPrice = buyPrice;
        this.stock = stock;
        this.warehouse = warehouse;
    }

    //todo: write setters and getters
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }
    public void setStock(int stock) {
        this.stock = stock;
    }
    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }
    public String getProductName(){
        return productName;
    }
    public double getBuyPrice() {
        return buyPrice;
    }
    public double getSellPrice() {
        return sellPrice;
    }
    public int getStock() {
        return stock;
    }
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void addStock(int amt) {
        this.stock += amt;
    }
}
