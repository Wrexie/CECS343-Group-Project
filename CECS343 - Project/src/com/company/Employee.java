package com.company;

public class Employee {
    private int employeeID;
    private String employeeName;
    private int phone;
    private double commRate;

    //creating new employee
    public Employee(String employeeName, int phone, double commRate) {
        this.employeeName = employeeName;
        this.phone = phone;
        this.commRate = commRate;
    }

    //load from DB
    public Employee(int employeeID, String employeeName, int phone, double commRate) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.phone = phone;
        this.commRate = commRate;
    }

    public void setCommRate(double commRate) {
        this.commRate = commRate;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public double getCommRate() {
        return commRate;
    }

    public int getEmployeeID() {
        return employeeID;
    }

    public int getPhone() {
        return phone;
    }
}
