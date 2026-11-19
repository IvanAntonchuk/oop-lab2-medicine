package ua.antonchuk.medicine.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppConfigTest {

    private static final Path XML = Path.of("data", "medicines.xml");
    private static final Path SCHEMA = Path.of("data", "medicines.xsd");
    private static final Path STYLESHEET = Path.of("data", "medicines-by-group.xsl");
    private static final Path OUTPUT = Path.of("output", "result.xml");

    private static final String VALID_PROPERTIES = """
            xml.path=data/medicines.xml
            xsd.path=data/medicines.xsd
            xsl.path=data/medicines-by-group.xsl
            output.path=output/result.xml
            """;

    @Test
    void constructorStoresAllPaths() {
        AppConfig config = new AppConfig(XML, SCHEMA, STYLESHEET, OUTPUT);

        assertEquals(XML, config.getXmlFile());
        assertEquals(SCHEMA, config.getSchemaFile());
        assertEquals(STYLESHEET, config.getStylesheetFile());
        assertEquals(OUTPUT, config.getOutputFile());
    }

    @Test
    void constructorRejectsNullXmlFile() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(null, SCHEMA, STYLESHEET, OUTPUT));
    }

    @Test
    void constructorRejectsNullSchemaFile() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(XML, null, STYLESHEET, OUTPUT));
    }

    @Test
    void constructorRejectsNullStylesheetFile() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(XML, SCHEMA, null, OUTPUT));
    }

    @Test
    void constructorRejectsNullOutputFile() {
        assertThrows(IllegalArgumentException.class,
                () -> new AppConfig(XML, SCHEMA, STYLESHEET, null));
    }

    @Test
    void loadReadsAllPathsFromPropertiesFile(@TempDir Path tempDir) throws IOException {
        Path file = write(tempDir, VALID_PROPERTIES);

        AppConfig config = AppConfig.load(file);

        assertEquals(XML, config.getXmlFile());
        assertEquals(SCHEMA, config.getSchemaFile());
        assertEquals(STYLESHEET, config.getStylesheetFile());
        assertEquals(OUTPUT, config.getOutputFile());
    }

    @Test
    void loadReadsProjectConfigFile() {
        AppConfig config = AppConfig.load(Path.of("config", "app.properties"));

        assertEquals(XML, config.getXmlFile());
        assertEquals(SCHEMA, config.getSchemaFile());
        assertEquals(STYLESHEET, config.getStylesheetFile());
        assertEquals(Path.of("output", "medicines-by-group.xml"), config.getOutputFile());
    }

    @Test
    void loadTrimsPropertyValues(@TempDir Path tempDir) throws IOException {
        Path file = write(tempDir,
                VALID_PROPERTIES.replace("data/medicines.xml", "data/medicines.xml   "));

        assertEquals(XML, AppConfig.load(file).getXmlFile());
    }

    @ParameterizedTest
    @ValueSource(strings = {"xml.path", "xsd.path", "xsl.path", "output.path"})
    void loadRejectsMissingProperty(String key, @TempDir Path tempDir) throws IOException {
        Path file = write(tempDir, VALID_PROPERTIES.lines()
                .filter(line -> !line.startsWith(key + "="))
                .collect(Collectors.joining("\n")));

        ConfigException exception = assertThrows(ConfigException.class,
                () -> AppConfig.load(file));

        assertEquals("Missing property: " + key, exception.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void loadRejectsBlankProperty(String value, @TempDir Path tempDir) throws IOException {
        Path file = write(tempDir, VALID_PROPERTIES.replace("data/medicines.xml", value));

        ConfigException exception = assertThrows(ConfigException.class,
                () -> AppConfig.load(file));

        assertEquals("Missing property: xml.path", exception.getMessage());
    }

    @Test
    void loadRejectsInvalidPath(@TempDir Path tempDir) throws IOException {
        Path file = write(tempDir, VALID_PROPERTIES.replace("data/medicines.xml", "a\\u0000b"));

        ConfigException exception = assertThrows(ConfigException.class,
                () -> AppConfig.load(file));

        assertTrue(exception.getMessage().startsWith("Invalid path in property xml.path: "));
    }

    @Test
    void loadRejectsMissingFile(@TempDir Path tempDir) {
        Path missing = tempDir.resolve("missing.properties");

        ConfigException exception = assertThrows(ConfigException.class,
                () -> AppConfig.load(missing));

        assertTrue(exception.getMessage().startsWith("Cannot read config file: "));
    }

    @Test
    void loadRejectsNullFile() {
        assertThrows(IllegalArgumentException.class, () -> AppConfig.load(null));
    }

    private static Path write(Path directory, String content) throws IOException {
        return Files.writeString(directory.resolve("app.properties"), content);
    }
}