package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;
import com.minibank.validation.ValidationAnnotations.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

public class BankService {
    private final AccountRepository accounts;
    private final AccountValidator validator;
    private final StatementPersister persister;
    private final TransactionLog log;
    private final TransactionProcessor processor;

    public BankService(AccountRepository accounts, AccountValidator validator,
                       StatementPersister persister, TransactionLog log,
                       TransactionProcessor processor) throws IOException {
        this.accounts = accounts;
        this.validator = validator;
        this.persister = persister;
        this.log = log;
        this.processor = processor;
        persister.loadAll(account -> {
            try { accounts.save(account); } catch (IllegalArgumentException ignored) { }
        });
    }

    @Audit
    public Account openAccount(String type, Customer customer, long openingBalance)
            throws BankException {
        if (openingBalance < 0) throw new InvalidAmountException("Opening balance cannot be negative.");
        String number = "MB" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Account account = switch (type.toLowerCase()) {
            case "savings" -> new SavingsAccount(number, customer, openingBalance, 500);
            case "current" -> new CurrentAccount(number, customer, openingBalance, 5_000);
            case "fixed" -> new FixedDepositAccount(number, customer, openingBalance);
            default -> throw new ValidationException("Account type must be savings, current, or fixed.");
        };
        validator.validate(account);
        try {
            accounts.save(account);
            persister.saveAccount(account);
            log.append(TransactionRecord.create(TransactionType.ACCOUNT_OPENED, number,
                    null, openingBalance, true, "Account opened"));
            return account;
        } catch (IOException exception) {
            throw new BankException("Could not save new account.", exception);
        }
    }

    @Audit
    public void deposit(String number, long amount) throws BankException {
        Account account = find(number);
        requirePositive(amount);
        account.deposit(amount);
        record(account, TransactionRecord.create(TransactionType.DEPOSIT, number,
                null, amount, true, "Deposit completed"));
    }

    @Audit
    public void withdraw(String number, long amount) throws BankException {
        Account account = find(number);
        requirePositive(amount);
        synchronized (account) {
            if (!withdrawalRulesAllow(account, amount) || !account.withdraw(amount)) {
                recordFailure(number, TransactionType.WITHDRAW, amount, "Withdrawal not allowed");
                throw new InsufficientFundsException("Withdrawal exceeds the account rule or balance.");
            }
        }
        record(account, TransactionRecord.create(TransactionType.WITHDRAW, number,
                null, amount, true, "Withdrawal completed"));
    }

    @Audit
    public void transfer(String sourceNumber, String destinationNumber, long amount)
            throws BankException {
        requirePositive(amount);
        Account source = find(sourceNumber);
        Account destination = find(destinationNumber);
        Account first = sourceNumber.compareTo(destinationNumber) < 0 ? source : destination;
        Account second = first == source ? destination : source;
        synchronized (first) {
            synchronized (second) {
                if (!source.canWithdraw(amount) || !source.withdraw(amount)) {
                    recordFailure(sourceNumber, TransactionType.TRANSFER, amount, "Transfer rejected");
                    throw new InsufficientFundsException("Source account cannot cover transfer.");
                }
                destination.deposit(amount);
            }
        }
        TransactionRecord sourceRecord = TransactionRecord.create(TransactionType.TRANSFER,
                sourceNumber, destinationNumber, amount, true, "Transfer completed");
        record(source, sourceRecord);
        try {
            persist(destination);
        } catch (IOException exception) {
            throw new BankException("Could not persist destination account.", exception);
        }
    }

    public Account find(String number) throws AccountNotFoundException {
        Account account = accounts.findById(number);
        if (account == null) throw new AccountNotFoundException("Account not found: " + number);
        return account;
    }

    public List<Account> allAccounts() { return accounts.findSortedByBalance(); }
    public List<TransactionRecord> statement(String number) throws BankException { return find(number).getTransactions(); }
    public Path generateReport(Path reports) throws IOException { return new ReportGenerator().write(log.path(), reports); }

    public TransactionProcessor processor() { return processor; }

    private boolean withdrawalRulesAllow(Account account, long amount) {
        WithdrawRule anonymousRule = new WithdrawRule() {
            @Override public boolean allow(Account value, long requested) {
                return value.canWithdraw(requested);
            }
        };
        WithdrawRule lambdaRule = Account::canWithdraw;
        return anonymousRule.allow(account, amount) && lambdaRule.allow(account, amount);
    }

    private void requirePositive(long amount) throws InvalidAmountException {
        if (amount <= 0) throw new InvalidAmountException("Amount must be positive.");
    }

    private void record(Account account, TransactionRecord record) throws BankException {
        account.addTransaction(record);
        try { persist(account); log.append(record); }
        catch (IOException exception) { throw new BankException("Could not persist transaction.", exception); }
    }

    private void persist(Account account) throws IOException {
        synchronized (account) {
            persister.saveAccount(account);
        }
    }

    private void recordFailure(String number, TransactionType type, long amount, String message)
            throws BankException {
        try { log.append(TransactionRecord.create(type, number, null, amount, false, message)); }
        catch (IOException exception) { throw new BankException("Could not write transaction log.", exception); }
    }
}
