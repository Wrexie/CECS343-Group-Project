package com.company;

public class Customer {

    private int id;
    private String firstName;
    private String lastName;
    private String shipAddress;
    private CustomerStatus status;
    private double taxRate;

    //use when creating new customer (ID generated in DB)
    public Customer(String firstName, String lastName, String shipAddress, CustomerStatus status, double taxRate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.shipAddress = shipAddress;
        this.status = status;
        this.taxRate = taxRate;
    }

    //load from DB constructor
    public Customer(int id, String firstName, String lastName, String shipAddress, CustomerStatus status, double taxRate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.shipAddress = shipAddress;
        this.status = status;
        this.taxRate = taxRate;
    }


    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
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
}
