package ua.antonchuk.medicine.parser.sax;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.parser.AbstractMedicineParser;
import ua.antonchuk.medicine.parser.MedicineEventHandler;
import ua.antonchuk.medicine.parser.XmlFactories;
import ua.antonchuk.medicine.parser.XmlValidator;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SaxMedicineParser extends AbstractMedicineParser {

    public SaxMedicineParser(XmlValidator validator) {
        super(validator);
    }

    @Override
    protected List<Medicine> parseDocument(InputStream input)
            throws IOException, SAXException, ParserConfigurationException {
        MedicineEventHandler handler = new MedicineEventHandler();
        XmlFactories.saxParserFactory().newSAXParser().parse(input, new SaxAdapter(handler));
        return handler.getMedicines();
    }

    private static final class SaxAdapter extends DefaultHandler {

        private final MedicineEventHandler handler;

        private SaxAdapter(MedicineEventHandler handler) {
            this.handler = handler;
        }

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) {
            handler.startElement(qName, attributes::getValue);
        }

        @Override
        public void characters(char[] chars, int start, int length) {
            handler.characters(new String(chars, start, length));
        }

        @Override
        public void endElement(String uri, String localName, String qName) {
            handler.endElement(qName);
        }
    }
}