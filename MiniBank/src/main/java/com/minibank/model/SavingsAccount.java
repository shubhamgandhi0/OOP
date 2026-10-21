package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class SavingsAccount extends Account implements Premium {
    private static final long serialVersionUID = 1L;
    private final long minBalance;

    public SavingsAccount(String number, Customer customer, long openingBalance, long minBalance) {
        super(number, customer, openingBalance);
        this.minBalance = minBalance;
    }

    public long getMinBalance() { return minBalance; }
    @Override public double interestRate() { return 4.0; }
    @Override public boolean canWithdraw(long amount) {
        return amount > 0 && getBalance() - amount >= minBalance;
    }
    @Override public String accountType() { return "Savings"; }
}
