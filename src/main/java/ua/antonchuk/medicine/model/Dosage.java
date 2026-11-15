package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.util.Locale;
import java.util.Objects;

public final class Dosage {

    private final DosageUnit unit;
    private final double amount;
    private final int timesPerDay;

    public Dosage(DosageUnit unit, double amount, int timesPerDay) {
        this.unit = Validation.requireNonNull(unit, "Dosage unit");
        this.amount = Validation.requirePositive(amount, "Amount");
        this.timesPerDay = Validation.requirePositive(timesPerDay, "Times per day");
    }

    public DosageUnit getUnit() {
        return unit;
    }

    public double getAmount() {
        return amount;
    }

    public int getTimesPerDay() {
        return timesPerDay;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Dosage that)) {
            return false;
        }
        return unit == that.unit
                && Double.compare(amount, that.amount) == 0
                && timesPerDay == that.timesPerDay;
    }

    @Override
    public int hashCode() {
        return Objects.hash(unit, amount, timesPerDay);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Dosage [amount=%s %s, timesPerDay=%d]", amount, unit, timesPerDay);
    }
}