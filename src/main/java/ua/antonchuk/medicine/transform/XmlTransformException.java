package ua.antonchuk.medicine.transform;

public class XmlTransformException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public XmlTransformException(String message) {
        super(message);
    }

    public XmlTransformException(String message, Throwable cause) {
        super(message, cause);
    }
}