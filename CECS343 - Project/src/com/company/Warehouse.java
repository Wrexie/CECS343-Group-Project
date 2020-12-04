package com.company;
public class Warehouse {

    private String name;
    private String address;
    private String phone;

    //constructor for warehouse
    public Warehouse(String name, String address, String phone) {
        this.name = name;
        this.address = address;
        this.phone = phone;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getName() {
        return name;
    }
    public String getAddress() {
        return address;
    }
    public String getPhone() {
        return phone;
    }
}