package ua.antonchuk.medicine.parser;

import org.junit.jupiter.api.Test;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.parser.dom.DomMedicineParser;
import ua.antonchuk.medicine.parser.sax.SaxMedicineParser;
import ua.antonchuk.medicine.parser.stax.StaxMedicineParser;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ParsersConsistencyTest {

    @Test
    void allParsersReturnTheSameMedicinesForSampleDocument() {
        XmlValidator validator = new XmlValidator(TestFiles.SCHEMA);

        List<Medicine> sax = new SaxMedicineParser(validator).parse(TestFiles.SAMPLE);
        List<Medicine> stax = new StaxMedicineParser(validator).parse(TestFiles.SAMPLE);
        List<Medicine> dom = new DomMedicineParser(validator).parse(TestFiles.SAMPLE);

        assertEquals(6, sax.size());
        assertEquals(sax, stax);
        assertEquals(sax, dom);
    }
}