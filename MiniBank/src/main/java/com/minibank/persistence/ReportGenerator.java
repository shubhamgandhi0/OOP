package com.minibank.persistence;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ReportGenerator {
    public String generate(Path log) throws IOException {
        long deposits = 0;
        long withdrawals = 0;
        if (Files.exists(log)) {
            for (String line : Files.readAllLines(log)) {
                String[] fields = line.split("\\|", -1);
                if (fields.length < 6 || !"true".equals(fields[5])) continue;
                long amount = Long.parseLong(fields[4]);
                if ("DEPOSIT".equals(fields[1])) deposits += amount;
                if ("WITHDRAW".equals(fields[1])) withdrawals += amount;
            }
        }
        return "MINIBANK REPORT%nSuccessful deposits: %d%nSuccessful withdrawals: %d%nNet change: %d%n"
                .formatted(deposits, withdrawals, deposits - withdrawals);
    }

    public Path write(Path log, Path reportsDirectory) throws IOException {
        Files.createDirectories(reportsDirectory);
        Path report = reportsDirectory.resolve("banking-report.txt");
        Files.writeString(report, generate(log));
        return report;
    }
}
