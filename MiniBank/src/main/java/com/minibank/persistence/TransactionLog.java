package com.minibank.persistence;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class TransactionLog {
    private final Path file;

    public TransactionLog(Path file) throws IOException {
        this.file = file;
        if (file.getParent() != null) Files.createDirectories(file.getParent());
        if (!Files.exists(file)) Files.createFile(file);
    }

    public synchronized void append(TransactionRecord record) throws IOException {
        String line = String.join("|", record.timestamp().toString(), record.type().name(),
                record.accountNumber(), record.otherAccount() == null ? "" : record.otherAccount(),
                Long.toString(record.amount()), Boolean.toString(record.successful()),
                record.message().replace("|", "/")) + System.lineSeparator();
        Files.writeString(file, line, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }

    public Path path() { return file; }
}
