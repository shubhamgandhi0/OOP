package com.minibank.util;

import com.minibank.exception.*;
import com.minibank.model.*;
import com.minibank.persistence.*;
import com.minibank.repository.*;
import com.minibank.service.*;
import com.minibank.util.*;
import com.minibank.validation.*;

import java.text.NumberFormat;
import java.util.Locale;

public final class MoneyFormatter {
    private MoneyFormatter() { }
    public static String format(long rupees) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN"))
                .format(rupees);
    }
}
