package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;
import com.minibank.validation.ValidationAnnotations.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Transactable, InterestBearing, Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String accountNumber;
    private final Customer customer;
    @Positive
    private long balance;
    private final List<TransactionRecord> transactions = new ArrayList<>();

    protected Account(String accountNumber, Customer customer, long openingBalance) {
        this.accountNumber = accountNumber;
        this.customer = customer;
        this.balance = openingBalance;
    }

    public abstract boolean canWithdraw(long amount);
    public abstract String accountType();

    public synchronized void deposit(long amount) {
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive.");
        balance += amount;
    }

    public synchronized boolean withdraw(long amount) {
        if (amount <= 0 || !canWithdraw(amount)) return false;
        balance -= amount;
        return true;
    }

    public synchronized long getBalance() { return balance; }
    public String getAccountNumber() { return accountNumber; }
    public Customer getCustomer() { return customer; }
    public synchronized List<TransactionRecord> getTransactions() {
        return new ArrayList<>(transactions);
    }
    public synchronized void addTransaction(TransactionRecord record) {
        transactions.add(record);
    }
    @Positive
    public synchronized long monthlyInterest() {
        return Math.round(balance * interestRate() / 100.0 / 12.0);
    }
    @Override
    public long balanceForInterest() { return getBalance(); }

    @Override
    public synchronized String toString() {
        return accountType() + " Account " + accountNumber + " | "
                + customer.getName() + " | balance " + balance;
    }
}
