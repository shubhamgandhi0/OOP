package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class CurrentAccount extends Account {
    private static final long serialVersionUID = 1L;
    private final long overdraftLimit;

    public CurrentAccount(String number, Customer customer, long openingBalance, long overdraftLimit) {
        super(number, customer, openingBalance);
        this.overdraftLimit = overdraftLimit;
    }

    public long getOverdraftLimit() { return overdraftLimit; }
    @Override public double interestRate() { return 0.0; }
    @Override public boolean canWithdraw(long amount) {
        return amount > 0 && getBalance() - amount >= -overdraftLimit;
    }
    @Override public String accountType() { return "Current"; }
}
