package ua.antonchuk.medicine.model;

import ua.antonchuk.medicine.util.Validation;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Objects;

public final class Certificate {

    private final String number;
    private final LocalDate issueDate;
    private final LocalDate expirationDate;
    private final String organization;

    public Certificate(String number, LocalDate issueDate, LocalDate expirationDate,
                       String organization) {
        this.number = Validation.requireText(number, "Number");
        this.issueDate = Validation.requireNonNull(issueDate, "Issue date");
        this.expirationDate = Validation.requireNonNull(expirationDate, "Expiration date");
        if (!expirationDate.isAfter(issueDate)) {
            throw new IllegalArgumentException(
                    "Expiration date must be after issue date: "
                            + issueDate + " - " + expirationDate);
        }
        this.organization = Validation.requireText(organization, "Organization");
    }

    public String getNumber() {
        return number;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public String getOrganization() {
        return organization;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Certificate that)) {
            return false;
        }
        return number.equals(that.number)
                && issueDate.equals(that.issueDate)
                && expirationDate.equals(that.expirationDate)
                && organization.equals(that.organization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, issueDate, expirationDate, organization);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "Certificate [number=%s, issued=%s, expires=%s, organization=%s]",
                number, issueDate, expirationDate, organization);
    }
}