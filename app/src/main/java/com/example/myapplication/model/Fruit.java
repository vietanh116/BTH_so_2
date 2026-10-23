package com.example.myapplication.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Fruit {
    private int id;
    private String name, category, description, expiryDate;
    private double price;
    private int quantity;

    public Fruit() {}

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    // Xử lý logic Hạn sử dụng
    public boolean isExpired() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date expDate = sdf.parse(this.expiryDate);
            Date currentDate = new Date();
            return expDate != null && expDate.before(currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return true; // Nếu lỗi định dạng ngày, coi như không an toàn
        }
    }

    public boolean isAvailableForSale() {
        return quantity > 0 && !isExpired();
    }
}