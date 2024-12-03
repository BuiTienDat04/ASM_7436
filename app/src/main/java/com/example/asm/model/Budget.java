package com.example.asm.model;

public class Budget {
    private String groupName;
    private String amount;
    private String date;

    public Budget(String groupName, String amount, String date) {
        this.groupName = groupName;
        this.amount = amount;
        this.date = date;
    }

    public String getGroupName() {
        return groupName;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}

