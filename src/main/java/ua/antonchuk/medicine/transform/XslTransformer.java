package ua.antonchuk.medicine.transform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.parser.XmlFactories;
import ua.antonchuk.medicine.util.Validation;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XslTransformer {

    private static final Logger LOGGER = LoggerFactory.getLogger(XslTransformer.class);

    private final Templates templates;

    public XslTransformer(Path stylesheet) {
        Validation.requireNonNull(stylesheet, "Stylesheet");
        try {
            TransformerFactory factory = XmlFactories.transformerFactory();
            factory.setErrorListener(new StrictErrorListener());
            this.templates = factory.newTemplates(new StreamSource(stylesheet.toFile()));
        } catch (TransformerConfigurationException e) {
            throw new XmlTransformException("Invalid XSL stylesheet: " + stylesheet, e);
        }
    }

    public void transform(Path xmlFile, Path outputFile) {
        Validation.requireNonNull(xmlFile, "XML file");
        Validation.requireNonNull(outputFile, "Output file");
        try {
            Transformer transformer = templates.newTransformer();
            transformer.setErrorListener(new StrictErrorListener());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            transformer.transform(new StreamSource(xmlFile.toFile()), new StreamResult(buffer));
            Files.createDirectories(outputFile.toAbsolutePath().getParent());
            Files.write(outputFile, buffer.toByteArray());
            LOGGER.info("Transformed {} into {}", xmlFile, outputFile);
        } catch (IOException e) {
            throw new XmlTransformException("Cannot write output file: " + outputFile, e);
        } catch (TransformerException e) {
            throw new XmlTransformException("Cannot transform XML file: " + xmlFile, e);
        }
    }
}