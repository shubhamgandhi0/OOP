package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

public record TransactionRecord(String id, Instant timestamp, TransactionType type,
                                String accountNumber, String otherAccount,
                                long amount, boolean successful, String message)
        implements Serializable {
    public static TransactionRecord create(TransactionType type, String account,
                                           String other, long amount,
                                           boolean successful, String message) {
        return new TransactionRecord(UUID.randomUUID().toString(), Instant.now(),
                type, account, other, amount, successful, message);
    }
}
