package org.vendingmachine.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int columnId;
    private String name;
    private float price;
    private int quantity;

    protected Product() {}

    public Product(int columnId, String name, float price, int quantity) {
        this.columnId = columnId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() { return id; }

    public int getColumnId() { return columnId; }
    public void setColumnId(int columnId) { this.columnId = columnId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void decrementQuantity(int amount) {
        this.quantity -= amount;
    }
}
