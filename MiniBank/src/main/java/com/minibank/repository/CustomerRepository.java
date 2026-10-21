package com.minibank.repository;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepository implements Repository<Customer> {
    private final ConcurrentHashMap<String, Customer> customers = new ConcurrentHashMap<>();
    @Override public void save(Customer customer) { customers.put(customer.getCustomerId(), customer); }
    @Override public Customer findById(String id) { return customers.get(id); }
    @Override public List<Customer> findAll() { return List.copyOf(customers.values()); }
}
