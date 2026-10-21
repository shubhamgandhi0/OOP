package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class FixedDepositAccount extends Account {
    private static final long serialVersionUID = 1L;

    public FixedDepositAccount(String number, Customer customer, long openingBalance) {
        super(number, customer, openingBalance);
    }

    @Override public double interestRate() { return 7.0; }
    @Override public boolean canWithdraw(long amount) { return false; }
    @Override public String accountType() { return "Fixed deposit"; }
}
