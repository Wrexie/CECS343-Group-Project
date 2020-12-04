package com.company;

public class Customer {

    private int id;
    private String fullName;
    private String shipAddress;
    private CustomerStatus status;
    private double taxRate;

    //use when creating new customer (ID generated in DB)
    public Customer(String fullName, String shipAddress, CustomerStatus status, double taxRate) {
        this.fullName = fullName;
        this.shipAddress = shipAddress;
        this.status = status;
        this.taxRate = taxRate;
    }

    //load from DB constructor
    public Customer(int id, String fullName, String shipAddress, CustomerStatus status, double taxRate) {
        this.id = id;
        this.fullName = fullName;
        this.shipAddress = shipAddress;
        this.status = status;
        this.taxRate = taxRate;
    }


    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getShipAddress() {
        return shipAddress;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public void setStatus(CustomerStatus status) {
        this.status = status;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setShipAddress(String shipAddress) {
        this.shipAddress = shipAddress;
    }
}
