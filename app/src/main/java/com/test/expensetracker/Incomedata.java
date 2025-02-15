package com.test.expensetracker;

import java.util.Date;

public class Incomedata {
    private String month;
    private String amount;
    private Date incomeDate;

    public Incomedata() {
        // Default constructor required for Firebase
    }

    public Incomedata(String month, String amount, Date incomeDate) {
        this.month = month;
        this.amount = amount;
        this.incomeDate = incomeDate;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }
}
