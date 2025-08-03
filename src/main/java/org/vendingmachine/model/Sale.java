package org.vendingmachine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int columnId;
    private float price;
    private String paymentMethod;
    private float changeGiven;
    private LocalDateTime timestamp;

    public Sale() {}

    public Sale(int columnId, float price, String paymentMethod, float changeGiven, LocalDateTime timestamp) {
        this.columnId = columnId;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.changeGiven = changeGiven;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public float getChangeGiven() {
        return changeGiven;
    }

    public void setChangeGiven(float changeGiven) {
        this.changeGiven = changeGiven;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
