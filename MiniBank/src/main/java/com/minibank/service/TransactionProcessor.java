package com.minibank.service;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransactionProcessor implements AutoCloseable {
    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public void shutdown() {
        executor.shutdown();
    }

    @Override public void close() { shutdown(); }
}
