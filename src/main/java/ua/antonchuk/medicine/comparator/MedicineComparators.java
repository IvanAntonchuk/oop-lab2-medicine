package ua.antonchuk.medicine.comparator;

import ua.antonchuk.medicine.model.Medicine;

import java.math.BigDecimal;
import java.util.Comparator;

public final class MedicineComparators {

    public static final Comparator<Medicine> BY_NAME =
            Comparator.comparing(Medicine::getName, String.CASE_INSENSITIVE_ORDER);

    public static final Comparator<Medicine> BY_GROUP_THEN_NAME =
            Comparator.comparing(Medicine::getGroup).thenComparing(BY_NAME);

    public static final Comparator<Medicine> BY_CHEAPEST_PRICE =
            Comparator.comparing(MedicineComparators::cheapestPrice).thenComparing(BY_NAME);

    private MedicineComparators() {
    }

    private static BigDecimal cheapestPrice(Medicine medicine) {
        return medicine.getVersions().stream()
                .flatMap(version -> version.getManufacturers().stream())
                .map(manufacturer -> manufacturer.getMedicinePackage().getPrice())
                .min(Comparator.naturalOrder())
                .orElseThrow();
    }
}