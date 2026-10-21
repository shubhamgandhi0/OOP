package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class CommandDispatcher {
    private final BankService bank;
    public CommandDispatcher(BankService bank) { this.bank = bank; }

    public void dispatch(Command command) throws BankException {
        if (command.type() == TransactionType.DEPOSIT) {
            bank.deposit(command.accountNumber(), command.amount());
        } else if (command.type() == TransactionType.WITHDRAW) {
            bank.withdraw(command.accountNumber(), command.amount());
        } else if (command.type() == TransactionType.TRANSFER) {
            bank.transfer(command.accountNumber(), command.destinationAccountNumber(), command.amount());
        } else {
            throw new IllegalArgumentException("This command is not an executable transaction.");
        }
    }
}
