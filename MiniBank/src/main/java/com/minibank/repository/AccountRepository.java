package com.minibank.repository;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;
import com.minibank.validation.ValidationAnnotations.*;

import java.lang.reflect.Field;
import java.util.List;

public class AccountRepository implements Repository<Account> {
    private final AccountStore store;
    public AccountRepository(AccountStore store) { this.store = store; }
    @Override public void save(Account account) {
        if (!account.getAccountNumber().equals(extractId(account))) {
            throw new IllegalArgumentException("Account identifier does not match @Id field.");
        }
        store.add(account);
    }
    @Override public Account findById(String id) { return store.find(id); }
    @Override public List<Account> findAll() { return store.all(); }
    public List<Account> findSortedByBalance() { return store.sortedByBalance(); }

    public <T extends Account> List<T> findAllOfType(Class<T> type) {
        return findAll().stream().filter(type::isInstance).map(type::cast).toList();
    }

    private String extractId(Account account) {
        for (Class<?> type = account.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                if (field.isAnnotationPresent(Id.class)) {
                    try {
                        field.setAccessible(true);
                        return (String) field.get(account);
                    } catch (ReflectiveOperationException exception) {
                        throw new IllegalArgumentException("Could not read account @Id.", exception);
                    }
                }
            }
        }
        throw new IllegalArgumentException("Account has no @Id field.");
    }

    @SafeVarargs
    public static <T extends Account> List<T> nonNullAccounts(T... accounts) {
        return java.util.Arrays.stream(accounts).filter(java.util.Objects::nonNull).toList();
    }
}
