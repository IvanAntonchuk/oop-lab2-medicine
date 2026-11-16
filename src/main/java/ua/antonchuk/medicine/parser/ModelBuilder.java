package ua.antonchuk.medicine.parser;

public abstract class ModelBuilder<T> {

    public final T build() {
        try {
            return create();
        } catch (IllegalArgumentException e) {
            throw new MedicineParseException(
                    "Invalid " + description() + ": " + e.getMessage(), e);
        }
    }

    protected abstract T create();

    protected abstract String description();
}