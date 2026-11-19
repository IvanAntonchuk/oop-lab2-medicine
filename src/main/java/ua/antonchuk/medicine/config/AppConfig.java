package ua.antonchuk.medicine.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.util.Validation;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.Properties;

public final class AppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppConfig.class);

    private static final String XML_KEY = "xml.path";
    private static final String SCHEMA_KEY = "xsd.path";
    private static final String STYLESHEET_KEY = "xsl.path";
    private static final String OUTPUT_KEY = "output.path";

    private final Path xmlFile;
    private final Path schemaFile;
    private final Path stylesheetFile;
    private final Path outputFile;

    public AppConfig(Path xmlFile, Path schemaFile, Path stylesheetFile, Path outputFile) {
        this.xmlFile = Validation.requireNonNull(xmlFile, "XML file");
        this.schemaFile = Validation.requireNonNull(schemaFile, "Schema file");
        this.stylesheetFile = Validation.requireNonNull(stylesheetFile, "Stylesheet file");
        this.outputFile = Validation.requireNonNull(outputFile, "Output file");
    }

    public static AppConfig load(Path propertiesFile) {
        Validation.requireNonNull(propertiesFile, "Properties file");
        Properties properties = new Properties();
        try (Reader reader = Files.newBufferedReader(propertiesFile, StandardCharsets.UTF_8)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new ConfigException("Cannot read config file: " + propertiesFile, e);
        }
        AppConfig config = new AppConfig(
                readPath(properties, XML_KEY),
                readPath(properties, SCHEMA_KEY),
                readPath(properties, STYLESHEET_KEY),
                readPath(properties, OUTPUT_KEY));
        LOGGER.info("Loaded configuration from {}", propertiesFile);
        return config;
    }

    private static Path readPath(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new ConfigException("Missing property: " + key);
        }
        try {
            return Path.of(value.trim());
        } catch (InvalidPathException e) {
            throw new ConfigException("Invalid path in property " + key + ": " + value, e);
        }
    }

    public Path getXmlFile() {
        return xmlFile;
    }

    public Path getSchemaFile() {
        return schemaFile;
    }

    public Path getStylesheetFile() {
        return stylesheetFile;
    }

    public Path getOutputFile() {
        return outputFile;
    }
}