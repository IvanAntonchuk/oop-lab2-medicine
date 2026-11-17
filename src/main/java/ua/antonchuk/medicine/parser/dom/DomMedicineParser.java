package ua.antonchuk.medicine.parser.dom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.parser.AbstractMedicineParser;
import ua.antonchuk.medicine.parser.MedicineEventHandler;
import ua.antonchuk.medicine.parser.XmlFactories;
import ua.antonchuk.medicine.parser.XmlValidator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DomMedicineParser extends AbstractMedicineParser {

    public DomMedicineParser(XmlValidator validator) {
        super(validator);
    }

    @Override
    protected List<Medicine> parseDocument(InputStream input)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder builder = XmlFactories.documentBuilderFactory().newDocumentBuilder();
        builder.setErrorHandler(new DefaultHandler());
        Document document = builder.parse(input);
        MedicineEventHandler handler = new MedicineEventHandler();
        walk(document.getDocumentElement(), handler);
        return handler.getMedicines();
    }

    private static void walk(Node node, MedicineEventHandler handler) {
        switch (node.getNodeType()) {
            case Node.ELEMENT_NODE -> {
                Element element = (Element) node;
                handler.startElement(element.getTagName(), element::getAttribute);
                for (Node child = node.getFirstChild(); child != null;
                     child = child.getNextSibling()) {
                    walk(child, handler);
                }
                handler.endElement(element.getTagName());
            }
            case Node.TEXT_NODE -> handler.characters(node.getNodeValue());
            default -> {
            }
        }
    }
}