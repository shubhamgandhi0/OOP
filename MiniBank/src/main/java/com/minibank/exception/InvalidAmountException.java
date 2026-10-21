package com.minibank.exception;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public class InvalidAmountException extends BankException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
