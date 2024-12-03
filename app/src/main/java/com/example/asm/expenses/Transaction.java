package com.example.asm.expenses;

import java.text.NumberFormat;
import java.util.Locale;

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

    // Getter cho số tiền định dạng với dấu phẩy và VND
    public String getFormattedAmount() {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
        return formatter.format(amount) + " VND";
    }
}
