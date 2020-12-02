package com.company;

public class Product {

    private int productID;
    private String productName;

    public Product(int productID, String productName){
        this.productID=productID;
        this.productName=productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductName(){
        return productName;
    }
    public int getProductID() {
        return productID;
    }

}
