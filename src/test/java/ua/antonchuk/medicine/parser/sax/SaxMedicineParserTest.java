package ua.antonchuk.medicine.parser.sax;

import ua.antonchuk.medicine.parser.MedicineParser;
import ua.antonchuk.medicine.parser.MedicineParserContractTest;
import ua.antonchuk.medicine.parser.XmlValidator;

class SaxMedicineParserTest extends MedicineParserContractTest {

    @Override
    protected MedicineParser createParser(XmlValidator validator) {
        return new SaxMedicineParser(validator);
    }
}