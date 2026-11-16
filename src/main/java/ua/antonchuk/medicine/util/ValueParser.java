package ua.antonchuk.medicine.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.function.Function;

public final class ValueParser {

    private ValueParser() {
    }

    public static LocalDate parseDate(String value, String fieldName) {
        return parse(value, fieldName, LocalDate::parse);
    }

    public static int parseInt(String value, String fieldName) {
        return parse(value, fieldName, Integer::parseInt);
    }

    public static double parseDouble(String value, String fieldName) {
        return parse(value, fieldName, Double::parseDouble);
    }

    public static BigDecimal parseDecimal(String value, String fieldName) {
        return parse(value, fieldName, BigDecimal::new);
    }

    private static <T> T parse(String value, String fieldName, Function<String, T> parser) {
        String text = Validation.requireText(value, fieldName);
        try {
            return parser.apply(text);
        } catch (DateTimeParseException | NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " has invalid value: " + text, e);
        }
    }
}