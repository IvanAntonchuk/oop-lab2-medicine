package ua.antonchuk.medicine.parser.stax;

import ua.antonchuk.medicine.parser.MedicineParser;
import ua.antonchuk.medicine.parser.MedicineParserContractTest;
import ua.antonchuk.medicine.parser.XmlValidator;

class StaxMedicineParserTest extends MedicineParserContractTest {

    @Override
    protected MedicineParser createParser(XmlValidator validator) {
        return new StaxMedicineParser(validator);
    }
}