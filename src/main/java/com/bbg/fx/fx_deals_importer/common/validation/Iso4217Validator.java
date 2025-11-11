package com.bbg.fx.fx_deals_importer.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Iso4217Validator implements ConstraintValidator<Iso4217, String> {

    private static final Set<String> CODES = Currency.getAvailableCurrencies()
            .stream()
            .map(Currency::getCurrencyCode)
            .collect(Collectors.toUnmodifiableSet());

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) return true; 
        String upper = value.toUpperCase(Locale.ROOT);
        return CODES.contains(upper);
    }
}