package ua.antonchuk.medicine.util;

import java.math.BigDecimal;
import java.util.List;

public final class Validation {

    private Validation() {
    }

    public static <T> T requireNonNull(T value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " must not be null");
        }
        return value;
    }

    public static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " must not be null or blank");
        }
        return value.trim();
    }

    public static int requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive: " + value);
        }
        return value;
    }

    public static double requirePositive(double value, String fieldName) {
        if (!(value > 0) || Double.isInfinite(value)) {
            throw new IllegalArgumentException(
                    fieldName + " must be a positive finite number: " + value);
        }
        return value;
    }

    public static BigDecimal requirePositive(BigDecimal value, String fieldName) {
        requireNonNull(value, fieldName);
        if (value.signum() <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive: " + value);
        }
        return value;
    }

    public static <T> List<T> copyOfList(List<T> values, String fieldName) {
        requireNonNull(values, fieldName);
        for (T value : values) {
            if (value == null) {
                throw new IllegalArgumentException(
                        fieldName + " must not contain null elements");
            }
        }
        return List.copyOf(values);
    }

    public static <T> List<T> requireNonEmptyList(List<T> values, String fieldName) {
        List<T> copy = copyOfList(values, fieldName);
        if (copy.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " must not be empty");
        }
        return copy;
    }
}