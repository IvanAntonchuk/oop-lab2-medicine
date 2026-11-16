package ua.antonchuk.medicine.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.util.Validation;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class AbstractMedicineParser implements MedicineParser {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractMedicineParser.class);

    private final XmlValidator validator;

    protected AbstractMedicineParser(XmlValidator validator) {
        this.validator = Validation.requireNonNull(validator, "Validator");
    }

    @Override
    public final List<Medicine> parse(Path xmlFile) {
        Validation.requireNonNull(xmlFile, "XML file");
        validator.validate(xmlFile);
        try (InputStream input = Files.newInputStream(xmlFile)) {
            List<Medicine> medicines = List.copyOf(parseDocument(input));
            LOGGER.info("{} parsed {} medicines from {}",
                    getClass().getSimpleName(), medicines.size(), xmlFile);
            return medicines;
        } catch (IOException e) {
            throw new MedicineParseException("Cannot read XML file: " + xmlFile, e);
        } catch (SAXException | ParserConfigurationException | XMLStreamException e) {
            throw new MedicineParseException("Cannot parse XML file: " + xmlFile, e);
        }
    }

    protected abstract List<Medicine> parseDocument(InputStream input)
            throws IOException, SAXException, ParserConfigurationException, XMLStreamException;
}