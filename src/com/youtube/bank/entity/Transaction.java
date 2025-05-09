package com.youtube.bank.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private LocalDateTime transactionDate;
    private Double transactionAmount;
    private String transactionType;
    private String userType;
    private Double initialBalance;
    private Double finalBalance;
    private String transactionPerformedBy;

    public Transaction(LocalDateTime transactionDate, Double transactionAmount, String transactionType, String userType, Double initialBalance, Double finalBalance, String transactionPerformedBy) {
        this.transactionDate = transactionDate;
        this.transactionAmount = transactionAmount;
        this.transactionType = transactionType;
        this.userType = userType;
        this.initialBalance = initialBalance;
        this.finalBalance = finalBalance;
        this.transactionPerformedBy = transactionPerformedBy;
    }

    @Override
    public String toString() {
        /*return "Transaction{" +
                "transactionDate=" + transactionDate +
                ", transactionAmount=" + transactionAmount +
                ", transactionType='" + transactionType + '\'' +
                ", initialBalance=" + initialBalance +
                ", finalBalance=" + finalBalance +
                ", transactionPerformedBy='" + transactionPerformedBy + '\'' +
                '}';*/


        return transactionDate + " : Amount " + transactionAmount + " Rs " + transactionType + userType + " Initial Balance: " + initialBalance + " Final Balance: " +finalBalance;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public Double getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(Double transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(Double initialBalance) {
        this.initialBalance = initialBalance;
    }

    public Double getFinalBalance() {
        return finalBalance;
    }

    public void setFinalBalance(Double finalBalance) {
        this.finalBalance = finalBalance;
    }

    public String getTransactionPerformedBy() {
        return transactionPerformedBy;
    }

    public void setTransactionPerformedBy(String transactionPerformedBy) {
        this.transactionPerformedBy = transactionPerformedBy;
    }
}
