package ua.antonchuk.medicine.parser;

public class MedicineParseException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public MedicineParseException(String message) {
        super(message);
    }

    public MedicineParseException(String message, Throwable cause) {
        super(message, cause);
    }
}