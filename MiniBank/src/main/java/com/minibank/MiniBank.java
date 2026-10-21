package com.minibank;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;

public class MiniBank {
    public static void main(String[] args) {
        Path data = Path.of("data");
        try {
            AccountStore store = new AccountStore();
            BankService bank = new BankService(new AccountRepository(store),
                    new AccountValidator(), new StatementPersister(data.resolve("accounts")),
                    new TransactionLog(data.resolve("transactions.log")),
                    new TransactionProcessor());
            runMenu(bank, new Scanner(System.in));
        } catch (IOException exception) {
            System.err.println("MiniBank could not start: " + exception.getMessage());
        }
    }

    private static void runMenu(BankService bank, Scanner scanner) {
        BankInfo info = new BankInfo("MiniBank", "Vadodara");
        System.out.println(info.name() + " - " + info.branch());
        boolean running = true;
        while (running) {
            printMenu();
            String input = scanner.nextLine();
            try {
                MenuOption option = MenuOption.fromNumber(Integer.parseInt(input.trim()));
                switch (option) {
                    case OPEN_ACCOUNT -> openAccount(bank, scanner);
                    case DEPOSIT -> changeBalance(bank, scanner, true);
                    case WITHDRAW -> changeBalance(bank, scanner, false);
                    case TRANSFER -> transfer(bank, scanner);
                    case BALANCE -> showBalance(bank, scanner);
                    case STATEMENT -> showStatement(bank, scanner);
                    case ALL_ACCOUNTS -> bank.allAccounts().forEach(System.out::println);
                    case REPORT -> System.out.println("Report: " + bank.generateReport(Path.of("reports")));
                    case EXIT -> running = false;
                }
            } catch (IllegalArgumentException exception) {
                System.out.println("Invalid choice or input: " + exception.getMessage());
            } catch (BankException | IOException exception) {
                System.out.println("Operation failed: " + exception.getMessage());
            }
        }
        bank.processor().shutdown();
        System.out.println("Goodbye.");
    }

    private static void printMenu() {
        System.out.println("""
                ----------------------------------------
                MINIBANK
                1. Open Account
                2. Deposit
                3. Withdraw
                4. Transfer Money
                5. View Account Details
                6. View Transaction Statement
                7. View All Accounts
                8. Generate Banking Report
                0. Exit
                ----------------------------------------
                Enter your choice:""");
    }

    private static void openAccount(BankService bank, Scanner scanner) throws BankException {
        System.out.print("Type (savings/current/fixed): ");
        String type = scanner.nextLine();
        System.out.print("Customer name: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Indian mobile (10 digits): ");
        String mobile = scanner.nextLine();
        System.out.print("Opening balance: ");
        long amount = Long.parseLong(scanner.nextLine());
        Account account = bank.openAccount(type, new Customer(name, email, mobile), amount);
        System.out.println("Created: " + account);
    }

    private static void changeBalance(BankService bank, Scanner scanner, boolean deposit)
            throws BankException {
        System.out.print("Account number: ");
        String number = scanner.nextLine();
        System.out.print("Amount: ");
        long amount = Long.parseLong(scanner.nextLine());
        if (deposit) bank.deposit(number, amount); else bank.withdraw(number, amount);
        System.out.println("New balance: " + MoneyFormatter.format(bank.find(number).getBalance()));
    }

    private static void transfer(BankService bank, Scanner scanner) throws BankException {
        System.out.print("Source account: ");
        String source = scanner.nextLine();
        System.out.print("Destination account: ");
        String destination = scanner.nextLine();
        System.out.print("Amount: ");
        bank.transfer(source, destination, Long.parseLong(scanner.nextLine()));
        System.out.println("Transfer completed.");
    }

    private static void showBalance(BankService bank, Scanner scanner) throws BankException {
        System.out.print("Account number: ");
        System.out.println(bank.find(scanner.nextLine()));
    }

    private static void showStatement(BankService bank, Scanner scanner) throws BankException {
        System.out.print("Account number: ");
        bank.statement(scanner.nextLine()).forEach(System.out::println);
    }
}
