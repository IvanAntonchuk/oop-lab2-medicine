package ua.antonchuk.medicine.parser.stax;

import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.parser.AbstractMedicineParser;
import ua.antonchuk.medicine.parser.MedicineEventHandler;
import ua.antonchuk.medicine.parser.XmlFactories;
import ua.antonchuk.medicine.parser.XmlValidator;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.util.List;

public class StaxMedicineParser extends AbstractMedicineParser {

    public StaxMedicineParser(XmlValidator validator) {
        super(validator);
    }

    @Override
    protected List<Medicine> parseDocument(InputStream input) throws XMLStreamException {
        MedicineEventHandler handler = new MedicineEventHandler();
        XMLStreamReader reader = XmlFactories.xmlInputFactory().createXMLStreamReader(input);
        try {
            while (reader.hasNext()) {
                switch (reader.next()) {
                    case XMLStreamConstants.START_ELEMENT -> handler.startElement(
                            reader.getLocalName(), name -> reader.getAttributeValue(null, name));
                    case XMLStreamConstants.CHARACTERS -> handler.characters(reader.getText());
                    case XMLStreamConstants.END_ELEMENT ->
                            handler.endElement(reader.getLocalName());
                    default -> {
                    }
                }
            }
        } finally {
            reader.close();
        }
        return handler.getMedicines();
    }
}