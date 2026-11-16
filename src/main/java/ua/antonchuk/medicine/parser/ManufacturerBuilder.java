package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Certificate;
import ua.antonchuk.medicine.model.Dosage;
import ua.antonchuk.medicine.model.DosageUnit;
import ua.antonchuk.medicine.model.Manufacturer;
import ua.antonchuk.medicine.model.MedicinePackage;
import ua.antonchuk.medicine.model.PackageKind;
import ua.antonchuk.medicine.util.EnumParser;
import ua.antonchuk.medicine.util.ValueParser;

public class ManufacturerBuilder extends ModelBuilder<Manufacturer> {

    private String name;
    private String certificateNumber;
    private String issued;
    private String expires;
    private String organization;
    private String packageType;
    private String quantity;
    private String price;
    private String dosageUnit;
    private String amount;
    private String timesPerDay;

    public ManufacturerBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ManufacturerBuilder certificate(String number, String issued, String expires) {
        this.certificateNumber = number;
        this.issued = issued;
        this.expires = expires;
        return this;
    }

    public ManufacturerBuilder organization(String organization) {
        this.organization = organization;
        return this;
    }

    public ManufacturerBuilder medicinePackage(String type, String quantity, String price) {
        this.packageType = type;
        this.quantity = quantity;
        this.price = price;
        return this;
    }

    public ManufacturerBuilder dosage(String unit, String amount, String timesPerDay) {
        this.dosageUnit = unit;
        this.amount = amount;
        this.timesPerDay = timesPerDay;
        return this;
    }

    @Override
    protected Manufacturer create() {
        Certificate certificate = new Certificate(certificateNumber,
                ValueParser.parseDate(issued, "Issue date"),
                ValueParser.parseDate(expires, "Expiration date"),
                organization);
        MedicinePackage medicinePackage = new MedicinePackage(
                EnumParser.parse(PackageKind.class, packageType),
                ValueParser.parseInt(quantity, "Quantity"),
                ValueParser.parseDecimal(price, "Price"));
        Dosage dosage = new Dosage(
                EnumParser.parse(DosageUnit.class, dosageUnit),
                ValueParser.parseDouble(amount, "Amount"),
                ValueParser.parseInt(timesPerDay, "Times per day"));
        return new Manufacturer(name, certificate, medicinePackage, dosage);
    }

    @Override
    protected String description() {
        return "manufacturer";
    }
}