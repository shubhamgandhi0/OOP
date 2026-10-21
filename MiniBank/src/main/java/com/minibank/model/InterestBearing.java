package com.minibank.model;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

public interface InterestBearing {
    double interestRate();

    default double yearlyInterest() {
        return balanceForInterest() * interestRate() / 100.0;
    }

    long balanceForInterest();
}
