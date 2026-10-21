package com.minibank;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.validation.*;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class BankServiceTest {
    @TempDir Path temporaryDirectory;

    @Test
    void savesAndReloadsAccountData() throws Exception {
        BankService bank = newBank();
        var account = bank.openAccount("savings",
                new Customer("Aarav", "aarav@example.com", "9876543210"), 1_000);
        bank.deposit(account.getAccountNumber(), 500);

        AccountStore restoredStore = new AccountStore();
        new StatementPersister(temporaryDirectory.resolve("accounts"))
                .loadAll(restoredStore::add);

        assertEquals(1_500, restoredStore.find(account.getAccountNumber()).getBalance());
        bank.processor().shutdown();
    }

    @Test
    void failedWithdrawalDoesNotChangeBalance() throws Exception {
        BankService bank = newBank();
        var account = bank.openAccount("savings",
                new Customer("Diya", "diya@example.com", "9876543210"), 1_000);
        assertThrows(InsufficientFundsException.class,
                () -> bank.withdraw(account.getAccountNumber(), 600));
        assertEquals(1_000, account.getBalance());
        bank.processor().shutdown();
    }

    @Test
    void oppositeTransfersFinishAndPreserveMoney() throws Exception {
        BankService bank = newBank();
        var first = bank.openAccount("current",
                new Customer("Kabir", "kabir@example.com", "9876543210"), 10_000);
        var second = bank.openAccount("current",
                new Customer("Maya", "maya@example.com", "9876543211"), 10_000);
        var futures = new java.util.ArrayList<java.util.concurrent.Future<?>>();
        for (int i = 0; i < 20; i++) {
            String source = i % 2 == 0 ? first.getAccountNumber() : second.getAccountNumber();
            String destination = i % 2 == 0 ? second.getAccountNumber() : first.getAccountNumber();
            futures.add(bank.processor().submit(
                    () -> {
                        try { bank.transfer(source, destination, 100); }
                        catch (Exception exception) { throw new RuntimeException(exception); }
                    }));
        }
        for (var future : futures) future.get(5, TimeUnit.SECONDS);
        assertEquals(20_000, first.getBalance() + second.getBalance());
        bank.processor().shutdown();
    }

    @Test
    void parserRejectsMalformedCommands() {
        assertThrows(IllegalArgumentException.class,
                () -> new CommandParser().parse("DEPOSIT only-one-field"));
        assertEquals("A2", new CommandParser()
                .parse("TRANSFER A1 A2 50").destinationAccountNumber());
    }

    private BankService newBank() throws Exception {
        Path accounts = temporaryDirectory.resolve("accounts");
        AccountStore store = new AccountStore();
        return new BankService(new AccountRepository(store), new AccountValidator(),
                new StatementPersister(accounts),
                new TransactionLog(temporaryDirectory.resolve("transactions.log")),
                new TransactionProcessor());
    }
}
