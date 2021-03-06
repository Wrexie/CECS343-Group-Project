package com.company;

import java.time.LocalDate;
import java.util.HashMap;

import static java.time.temporal.ChronoUnit.DAYS;


public class Invoice {
    private static final double DISCOUNT = 0.10;
    private static final int DAYS_FOR_DISCOUNT = 10;
    private static final double INTEREST_RATE = 0.02;

    private int invoiceID;
    private double total;
    private double deliveryFee;
    private double taxAmt;
    private double commAmount;
    private double owed;
    private int thirtyDayCount; //saves number of 30 day cycle's has passed (probably don't display to user in UI)
    private boolean isDeliverable;
    private LocalDate openedDate; //
    private Employee employee;
    private Customer customer;
    private HashMap<Integer, Integer> prodList; // change to product
    private InvoiceStatus status;


    //constructor for adding an unpaid invoice
    public Invoice(boolean isDeliverable, double deliveryFee, LocalDate openedDate, Customer customer, Employee employee) throws IllegalArgumentException{

        if(customer.getStatus().equals(CustomerStatus.SUSPENDED)) {
            throw new IllegalArgumentException("Cannot make unpaid invoice for suspended customer.");
        } else if (customer.getStatus().equals(CustomerStatus.INACTIVE)) {
            throw new IllegalArgumentException("Cannot make unpaid invoice for inactive customer");
        }
        this.total = 0;
        this.owed = 0;
        this.taxAmt = 0;
        this.commAmount = 0;
        this.isDeliverable = isDeliverable;
        this.deliveryFee = deliveryFee;
        this.customer = customer;
        this.prodList =  new HashMap<>();
        this.status = InvoiceStatus.UNPAID;
        this.thirtyDayCount = 0;
        this.openedDate = openedDate;
        this.employee = employee;
    }

    //constructor for adding a new paid invoice
    public Invoice(double total, boolean isDeliverable, double deliveryFee, LocalDate openedDate, Customer customer, Employee employee) {
        this.total = total;
        this.owed = 0;
        this.commAmount = total*employee.getCommRate();
        this.taxAmt = total*customer.getTaxRate();
        this.isDeliverable = isDeliverable;
        this.deliveryFee = deliveryFee;
        this.customer = customer;
        this.prodList = new HashMap<>();
        this.status = InvoiceStatus.PAID;
        this.thirtyDayCount = (int) DAYS.between(openedDate, LocalDate.now()) / 30;
        this.openedDate = openedDate;
        this.employee = employee;
    }

    //constructor for loading a customer from database for use
    public Invoice(int invoiceID, double total, double owed, boolean isDeliverable, double deliveryFee, int thirtyDayCount, LocalDate openedDate, Customer customer, Employee employee, HashMap<Integer, Integer> prodList, InvoiceStatus status) {
        this.invoiceID = invoiceID;
        this.total = total;
        this.owed = owed;
        this.taxAmt = total*customer.getTaxRate();
        this.isDeliverable = isDeliverable;
        this.deliveryFee = deliveryFee;
        this.customer = customer;
        this.prodList = prodList;
        this.status = status;
        this.thirtyDayCount = thirtyDayCount;
        this.openedDate = openedDate;
        this.employee = employee;
    }


    public Product addProduct(Product product, int quantity) {

            if(product.getStock()-quantity >= 0) { //check stock of prod before adding
                prodList.put(product.getProductID(), quantity);
                for(int i = 0; i < quantity; i++) {
                    product.addStock(-1);
                    owed += product.getSellPrice()*(1.0+customer.getTaxRate());
                    total += product.getSellPrice()*(1.0+customer.getTaxRate());
                    commAmount += product.getSellPrice()*employee.getCommRate();
                }
                taxAmt = total*customer.getTaxRate();
            } else {
            System.out.println("Not enough stock! Aborted.");
            }

        return product;
    }

    public void makePayment(double value) {
        if(!(value < 0)) {

            owed -= value;

            //tasks performed after invoice is paid off
            if(owed<0) {
                owed = 0;
            }
            if(owed==0 && status.equals(InvoiceStatus.UNPAID)) { //closing the invoice and seeing if it meets requirements for discount
                status = InvoiceStatus.PAID;
                checkDiscount();
            }
        } else {
            System.out.println("Must enter a positive amount!\n");
        }
    }


    /*
        Called from within the DB on Invoices with UNPAID status.
        (Should be called everytime at the beginning of the menu loop)
        Checks whether or not 2% should be added to total
     */
    public void checkPenalty() {
        if(status.equals(InvoiceStatus.UNPAID)) {
            if(openedDate != null) {

               int cycleCount = (int) DAYS.between(openedDate, LocalDate.now()) / 30;
               if(cycleCount > thirtyDayCount) {
                   double amtToAdd;
                   for(int i = 0; i < (cycleCount-thirtyDayCount); i++){ //to account for situations where checkPenalty has not been called for >1 30 day periods
                       amtToAdd = owed*INTEREST_RATE;
                       owed += amtToAdd;
                       total += amtToAdd;
                   }

                   thirtyDayCount = cycleCount;
                   taxAmt = total*customer.getTaxRate();
               }
                if(thirtyDayCount > 0) {
                    customer.setStatus(CustomerStatus.SUSPENDED);
                }
            }
        } else {
            System.out.println("Must be used on an UNPAID invoice");
        }
    }

    private void checkDiscount() { //use when invoice is paid in full in less than 10 days
        if(openedDate != null) {

            if(status.equals(InvoiceStatus.PAID)) { //if invoice is paid check
                long diff = DAYS.between(openedDate,LocalDate.now());

                if(diff < DAYS_FOR_DISCOUNT) {
                    total -= (total*DISCOUNT);
                    System.out.println("Discount applied to invoice! ID: " + invoiceID);
                }
            }

        }
    }

    public int getInvoiceID() { return invoiceID; }

    public InvoiceStatus getStatus() {
        return status;
    }

    public double getTotal(){
        return total;
    }

    public double getDeliveryFee() {
        return deliveryFee;
    }

    public double getOwed() {
        return owed;
    }

    public double getTaxAmt() {
        return taxAmt;
    }

    public double getCommAmount() { return commAmount; }

    public boolean getIsDeliverable() {return isDeliverable;}

    public HashMap<Integer, Integer> getProdList() {
        return prodList;
    }

    public boolean isDeliverable() {
        return isDeliverable;
    }

    public int getThirtyDayCount() {
        return thirtyDayCount;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Employee getEmployee() {
        return employee;
    }

    public LocalDate getOpenedDate() {
        return openedDate;
    }


}
