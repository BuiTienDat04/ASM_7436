package com.example.asm.model;

public class BudgetItem {
    private String groupName;
    private String amount;
    private String dateRange;

    public BudgetItem(String groupName, String amount, String dateRange) {
        this.groupName = groupName;
        this.amount = amount;
        this.dateRange = dateRange;
    }

    public String getGroupName() { return groupName; }
    public String getAmount() { return amount; }
    public String getDateRange() { return dateRange; }
}
