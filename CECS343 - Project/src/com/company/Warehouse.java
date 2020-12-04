package com.company;
public class Warehouse {

    private String name;
    private int stock;
    private String address;
    private String phone;

    public void setAddress(String address) {
        this.address = address;
    }
    public void addStock(int stock){
        stock=stock+this.stock;
    }
    public int showStock(){
        return stock;
    }
    public void minusStock(int stock) {
        stock=stock-this.stock;
    }

}