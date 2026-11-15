package ua.antonchuk.medicine.parser;

import org.xml.sax.SAXException;
import ua.antonchuk.medicine.util.Validation;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class XmlValidator {

    private final Schema schema;

    public XmlValidator(Path schemaFile) {
        Validation.requireNonNull(schemaFile, "Schema file");
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            this.schema = factory.newSchema(schemaFile.toFile());
        } catch (SAXException e) {
            throw new MedicineParseException("Invalid XSD schema: " + schemaFile, e);
        }
    }

    public void validate(Path xmlFile) {
        Validation.requireNonNull(xmlFile, "XML file");
        try (InputStream input = Files.newInputStream(xmlFile)) {
            Validator validator = schema.newValidator();
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
            validator.validate(new StreamSource(input));
        } catch (SAXException e) {
            throw new MedicineParseException(
                    "XML file is not valid: " + xmlFile + " - " + e.getMessage(), e);
        } catch (IOException e) {
            throw new MedicineParseException("Cannot read XML file: " + xmlFile, e);
        }
    }
}