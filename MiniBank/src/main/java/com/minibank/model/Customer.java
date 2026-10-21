package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;
import com.minibank.validation.ValidationAnnotations.*;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Customer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private final String customerId;
    @MaxLength(60)
    private final String name;
    private final String email;
    private final String mobile;
    private Address address;

    public Customer(String name, String email, String mobile) {
        this(UUID.randomUUID().toString(), name, email, mobile);
    }

    public Customer(String customerId, String name, String email, String mobile) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMobile() { return mobile; }
    public Address getAddress() { return address; }
    public void setAddress(Address address) { this.address = address; }

    @Override
    public boolean equals(Object object) {
        return object instanceof Customer other && customerId.equals(other.customerId);
    }

    @Override
    public int hashCode() { return Objects.hash(customerId); }

    @Override
    public String toString() {
        return name + " (" + customerId + ", " + mobile + ")";
    }

    public record Address(String street, String city, String postalCode)
            implements Serializable {
    }
}
