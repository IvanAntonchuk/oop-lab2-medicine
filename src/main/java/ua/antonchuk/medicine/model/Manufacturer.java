package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.util.Locale;
import java.util.Objects;

public final class Manufacturer {

    private final String name;
    private final Certificate certificate;
    private final MedicinePackage medicinePackage;
    private final Dosage dosage;

    public Manufacturer(String name, Certificate certificate, MedicinePackage medicinePackage,
                        Dosage dosage) {
        this.name = Validation.requireText(name, "Manufacturer name");
        this.certificate = Validation.requireNonNull(certificate, "Certificate");
        this.medicinePackage = Validation.requireNonNull(medicinePackage, "Package");
        this.dosage = Validation.requireNonNull(dosage, "Dosage");
    }

    public String getName() {
        return name;
    }

    public Certificate getCertificate() {
        return certificate;
    }

    public MedicinePackage getMedicinePackage() {
        return medicinePackage;
    }

    public Dosage getDosage() {
        return dosage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Manufacturer that)) {
            return false;
        }
        return name.equals(that.name)
                && certificate.equals(that.certificate)
                && medicinePackage.equals(that.medicinePackage)
                && dosage.equals(that.dosage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, certificate, medicinePackage, dosage);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Manufacturer [name=%s, certificate=%s, package=%s, dosage=%s]",
                name, certificate, medicinePackage, dosage);
    }
}