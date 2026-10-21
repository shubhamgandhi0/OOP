package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public enum MenuOption {
    OPEN_ACCOUNT(1), DEPOSIT(2), WITHDRAW(3), TRANSFER(4), BALANCE(5),
    STATEMENT(6), ALL_ACCOUNTS(7), REPORT(8), EXIT(0);

    private final int number;

    MenuOption(int number) {
        this.number = number;
    }

    public static MenuOption fromNumber(int number) {
        return switch (number) {
            case 1 -> OPEN_ACCOUNT;
            case 2 -> DEPOSIT;
            case 3 -> WITHDRAW;
            case 4 -> TRANSFER;
            case 5 -> BALANCE;
            case 6 -> STATEMENT;
            case 7 -> ALL_ACCOUNTS;
            case 8 -> REPORT;
            case 0 -> EXIT;
            default -> throw new IllegalArgumentException("Unknown menu choice.");
        };
    }

    public int number() {
        return number;
    }
}
