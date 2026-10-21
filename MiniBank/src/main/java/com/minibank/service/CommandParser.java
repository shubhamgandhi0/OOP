package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class CommandParser {
    public Command parse(String line) {
        String[] fields = line == null ? new String[0] : line.trim().split("\\s+");
        if (fields.length < 3) throw new IllegalArgumentException(
                "Use: DEPOSIT account amount, WITHDRAW account amount, or TRANSFER source destination amount");
        TransactionType type;
        try {
            type = TransactionType.valueOf(fields[0].toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Unknown transaction type: " + fields[0]);
        }
        try {
            if (type == TransactionType.TRANSFER) {
                if (fields.length != 4) throw new IllegalArgumentException("Transfer needs source, destination, and amount.");
                return new Command(type, fields[1], Long.parseLong(fields[3]), fields[2]);
            }
            if (fields.length != 3) throw new IllegalArgumentException("Transaction needs account and amount.");
            return new Command(type, fields[1], Long.parseLong(fields[2]), null);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Amount must be a whole number.");
        }
    }
}
