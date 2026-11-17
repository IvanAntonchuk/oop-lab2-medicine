package ua.antonchuk.medicine.parser.dom;

import ua.antonchuk.medicine.parser.MedicineParser;
import ua.antonchuk.medicine.parser.MedicineParserContractTest;
import ua.antonchuk.medicine.parser.XmlValidator;

class DomMedicineParserTest extends MedicineParserContractTest {

    @Override
    protected MedicineParser createParser(XmlValidator validator) {
        return new DomMedicineParser(validator);
    }
}