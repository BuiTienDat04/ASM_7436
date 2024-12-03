package com.example.asm.expenses;

public class Transaction {
    private int id;
    private String description;
    private double amount;
    private String date;
    private String budgetName;

    public Transaction(int id, String description, double amount, String date, String budgetName) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.budgetName = budgetName;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getBudgetName() {
        return budgetName;
    }
}
