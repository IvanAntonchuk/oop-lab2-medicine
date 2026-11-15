package ua.antonchuk.medicine.parser;

import java.nio.file.Path;

final class TestFiles {

    static final Path SCHEMA = Path.of("data", "medicines.xsd");
    static final Path SAMPLE = Path.of("data", "medicines.xml");

    private TestFiles() {
    }

    static Path fixture(String name) {
        return Path.of("src", "test", "resources", "xml", name);
    }
}