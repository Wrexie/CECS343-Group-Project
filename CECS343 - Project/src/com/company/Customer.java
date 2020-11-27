package com.company;

public class Customer {

    private int id;
    private String customerName;
    private String shipAddress;
    private CustomerStatus status;
    private double taxRate;


    //load from DB constructor
    public Customer(int id, String customerName, String shipAddress, CustomerStatus status, double taxRate) {
        this.id = id;
        this.customerName = customerName;
        this.shipAddress = shipAddress;
        this.status = status;
        this.taxRate = taxRate;
    }


    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
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
}
