package com.minibank.validation;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;
import com.minibank.validation.ValidationAnnotations.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AccountValidator {
    private static final Pattern MOBILE = Pattern.compile("[6-9][0-9]{9}");
    private static final Pattern ID = Pattern.compile("[A-Za-z0-9-]{3,40}");

    public boolean isValidMobile(String mobile) {
        return mobile != null && MOBILE.matcher(mobile).matches();
    }

    public boolean isValidId(String id) {
        return id != null && ID.matcher(id).matches();
    }

    public void validate(Account account) throws ValidationException {
        List<String> errors = new ArrayList<>();
        for (Class<?> type = account.getClass(); type != null; type = type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    Object value = field.get(account);
                    if (field.isAnnotationPresent(Positive.class)
                            && value instanceof Number number && number.longValue() < 0) {
                        errors.add(field.getName() + " must not be negative");
                    }
                    MaxLength max = field.getAnnotation(MaxLength.class);
                    if (max != null && value instanceof String text && text.length() > max.value()) {
                        errors.add(field.getName() + " exceeds " + max.value() + " characters");
                    }
                } catch (ReflectiveOperationException exception) {
                    errors.add("Could not inspect " + field.getName());
                }
            }
        }
        if (!isValidId(account.getAccountNumber())) errors.add("Invalid account number");
        Customer customer = account.getCustomer();
        if (customer == null || customer.getName() == null || customer.getName().isBlank()) {
            errors.add("Customer name is required");
        } else if (!isValidMobile(customer.getMobile())) {
            errors.add("Mobile must be a valid Indian ten-digit number");
        }
        if (!errors.isEmpty()) throw new ValidationException(String.join("; ", errors));
    }
}
