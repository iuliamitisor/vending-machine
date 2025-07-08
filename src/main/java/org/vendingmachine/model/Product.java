package org.vendingmachine.model;

public class Product {
    private int columnId;
    private String name;
    private float price;
    private int quantity;

    public Product(int columnId, String name, float price, int quantity) {
        this.columnId = columnId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

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
