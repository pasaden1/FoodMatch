package com.botevplovdiv.foodmatch;

/**
 * Created by Stelio on 2.6.2017 г..
 */

public class Order {

    private String currentTime;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String customerOrder;

    public Order(){

    }

    public Order(String customerName, String customerAddress, String customerPhone, String customerOrder,String currentTime) {
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.customerPhone = customerPhone;
        this.customerOrder = customerOrder;
        this.currentTime = currentTime;

    }

    //getters


    public String getCurrentTime() {
        return currentTime;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public String getCustomerOrder() {
        return customerOrder;
    }



    //setters


    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public void setCustomerOrder(String customerOrder) {
        this.customerOrder = customerOrder;
    }
}
