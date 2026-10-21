package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class AccountWorker implements Runnable {
    private final Account account;
    private final long amount;
    private final boolean deposit;

    public AccountWorker(Account account, long amount, boolean deposit) {
        this.account = account;
        this.amount = amount;
        this.deposit = deposit;
    }

    @Override public void run() {
        if (deposit) account.deposit(amount);
        else account.withdraw(amount);
    }
}
