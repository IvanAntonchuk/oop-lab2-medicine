package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Objects;

public final class MedicinePackage {

    private final PackageKind type;
    private final int quantity;
    private final BigDecimal price;

    public MedicinePackage(PackageKind type, int quantity, BigDecimal price) {
        this.type = Validation.requireNonNull(type, "Package kind");
        this.quantity = Validation.requirePositive(quantity, "Quantity");
        this.price = Validation.requirePositive(price, "Price");
    }

    public PackageKind getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MedicinePackage that)) {
            return false;
        }
        return type == that.type
                && quantity == that.quantity
                && price.compareTo(that.price) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, quantity, price.stripTrailingZeros());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "MedicinePackage [type=%s, quantity=%d, price=%s]",
                type, quantity, price.toPlainString());
    }
}