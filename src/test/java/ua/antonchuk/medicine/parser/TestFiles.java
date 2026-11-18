package ua.antonchuk.medicine.parser;

import java.nio.file.Path;

public final class TestFiles {

    public static final Path SCHEMA = Path.of("data", "medicines.xsd");
    public static final Path SAMPLE = Path.of("data", "medicines.xml");
    public static final Path STYLESHEET = Path.of("data", "medicines-by-group.xsl");

    private TestFiles() {
    }

    public static Path fixture(String name) {
        return Path.of("src", "test", "resources", "xml", name);
    }
}