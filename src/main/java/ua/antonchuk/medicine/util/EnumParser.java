package ua.antonchuk.medicine.util;

public final class EnumParser {

    private EnumParser() {
    }

    public static <E extends Enum<E>> E parse(Class<E> enumType, String value) {
        if (enumType == null) {
            throw new IllegalArgumentException("Enum type must not be null");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    enumType.getSimpleName() + " value must not be null or blank");
        }
        String normalized = value.trim();
        for (E constant : enumType.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(normalized)) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + enumType.getSimpleName() + " value: " + value);
    }
}