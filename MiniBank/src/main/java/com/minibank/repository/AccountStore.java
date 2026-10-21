package com.minibank.repository;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AccountStore {
    private final ConcurrentHashMap<String, Account> accounts = new ConcurrentHashMap<>();

    public void add(Account account) {
        if (accounts.putIfAbsent(account.getAccountNumber(), account) != null) {
            throw new IllegalArgumentException("Duplicate account number.");
        }
    }

    public Account find(String number) { return accounts.get(number); }
    public List<Account> all() { return new ArrayList<>(accounts.values()); }
    public List<Account> sortedByBalance() {
        return accounts.values().stream()
                .sorted(Comparator.comparingLong(Account::getBalance).reversed()).toList();
    }
}
