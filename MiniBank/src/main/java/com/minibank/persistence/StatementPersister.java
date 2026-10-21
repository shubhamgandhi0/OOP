package com.minibank.persistence;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class StatementPersister {
    private final Path directory;

    public StatementPersister(Path directory) throws IOException {
        this.directory = directory;
        Files.createDirectories(directory);
    }

    public void saveAccount(Account account) throws IOException {
        try (ObjectOutputStream output = new ObjectOutputStream(
                Files.newOutputStream(directory.resolve(account.getAccountNumber() + ".ser")))) {
            output.writeObject(account);
        }
    }

    public Account loadAccount(String accountNumber) throws IOException {
        Path file = directory.resolve(accountNumber + ".ser");
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(file))) {
            return (Account) input.readObject();
        } catch (ClassNotFoundException | ClassCastException exception) {
            throw new IOException("Corrupted account file: " + file, exception);
        }
    }

    public void loadAll(java.util.function.Consumer<Account> consumer) throws IOException {
        try (var files = Files.list(directory)) {
            for (Path file : files.filter(path -> path.toString().endsWith(".ser")).toList()) {
                consumer.accept(loadAccount(file.getFileName().toString().replace(".ser", "")));
            }
        }
    }
}
