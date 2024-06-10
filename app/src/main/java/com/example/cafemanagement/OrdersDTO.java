package com.example.cafemanagement;

import java.io.Serializable;

public class OrdersDTO implements Serializable {
    private String order_num;
    private String order_date;
    private  int menu_id;
    private String menu_name;
    private int amount;
    private int total_price;


    private int day_amount;
    private int day_sales;
    private int month_amount;
    private int month_sales;

    public OrdersDTO(String order_num, String order_date, String menu_name, int amount, int total_price) {
        this.order_num = order_num;
        this.order_date = order_date;
        this.menu_name = menu_name;
        this.amount = amount;
        this.total_price = total_price;
    }

    public OrdersDTO(String order_date, String menu_name, int day_amount, int day_sales, int month_amount, int month_sales) {
        this.order_date = order_date;
        this.menu_name = menu_name;
        this.day_amount = day_amount;
        this.day_sales = day_sales;
        this.month_amount = month_amount;
        this.month_sales = month_sales;
    }

    public String toString() {
        return "OrdersDTO{" +
                "order_num=" + order_num +
                ", order_date=" + order_date +
                ", menu_name=" + menu_name +
                ", amount=" + amount +
                ", total_price=" + total_price +
                '}';
    }

    public String getOrder_num() {
        return order_num;
    }

    public void setOrder_num(String order_num) {
        this.order_num = order_num;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(int menu_id) {
        this.menu_id = menu_id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getTotal_price() {
        return total_price;
    }

    public void setTotal_price(int total_price) {
        this.total_price = total_price;
    }
    public String getMenu_name() {
        return menu_name;
    }

    public void setMenu_name(String menu_name) {
        this.menu_name = menu_name;
    }

    public int getDay_amount() {
        return day_amount;
    }

    public void setDay_amount(int day_amount) {
        this.day_amount = day_amount;
    }

    public int getDay_sales() {
        return day_sales;
    }

    public void setDay_sales(int day_sales) {
        this.day_sales = day_sales;
    }

    public int getMonth_amount() {
        return month_amount;
    }

    public void setMonth_amount(int month_amount) {
        this.month_amount = month_amount;
    }

    public int getMonth_sales() {
        return month_sales;
    }

    public void setMonth_sales(int month_sales) {
        this.month_sales = month_sales;
    }

}
