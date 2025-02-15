package com.test.expensetracker;

import java.util.Date;

public class Expense {
    private String description;
    private String amount;
    private String dateTime;

    public Expense() {
        // Default constructor required for Firebase
    }

    public Expense(String description, String amount, String dateTime) {
        this.description = description;
        this.amount = amount;
        this.dateTime = dateTime;
    }


    public String getDescription() {
        return description;
    }

    public String getAmount() {
        return amount;
    }
    public String getDateTime() {
        return dateTime;
    }

}
