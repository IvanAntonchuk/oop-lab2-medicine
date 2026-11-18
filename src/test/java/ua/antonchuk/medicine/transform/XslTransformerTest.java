package ua.antonchuk.medicine.transform;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ua.antonchuk.medicine.parser.TestFiles;
import ua.antonchuk.medicine.parser.XmlFactories;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XslTransformerTest {

    private static final String THREE_MEDICINES = """
            <Medicines>
                <Medicine id="MED-001"><Group>vitamin</Group></Medicine>
                <Medicine id="MED-002"><Group>antibiotic</Group></Medicine>
                <Medicine id="MED-003"><Group>vitamin</Group></Medicine>
            </Medicines>
            """;

    private final XslTransformer transformer = new XslTransformer(TestFiles.STYLESHEET);

    @Test
    void transformCreatesGroupElementsInAlphabeticalOrder(@TempDir Path tempDir) throws Exception {
        Element root = transformToRoot(TestFiles.SAMPLE, tempDir);

        assertEquals("Medicines", root.getTagName());
        assertEquals(List.of("analgesic", "antibiotic", "vitamin"), tagNames(childElements(root)));
    }

    @Test
    void transformPlacesEachMedicineInElementNamedAfterItsGroup(@TempDir Path tempDir)
            throws Exception {
        Element root = transformToRoot(TestFiles.SAMPLE, tempDir);

        for (Element group : childElements(root)) {
            List<Element> medicines = childElements(group);
            assertEquals(2, medicines.size());
            for (Element medicine : medicines) {
                assertEquals("Medicine", medicine.getTagName());
                assertEquals(group.getTagName(), childText(medicine, "Group"));
            }
        }
    }

    @Test
    void transformKeepsAllMedicinesAndTheirNestedData(@TempDir Path tempDir) throws Exception {
        Element root = transformToRoot(TestFiles.SAMPLE, tempDir);
        Element source = read(TestFiles.SAMPLE).getDocumentElement();

        List<String> ids = new ArrayList<>();
        for (Element group : childElements(root)) {
            for (Element medicine : childElements(group)) {
                ids.add(medicine.getAttribute("id"));
            }
        }
        ids.sort(String::compareTo);

        assertEquals(List.of("MED-001", "MED-002", "MED-003", "MED-004", "MED-005", "MED-006"), ids);
        assertEquals(count(source, "Manufacturer"), count(root, "Manufacturer"));
        assertEquals(count(source, "Package"), count(root, "Package"));
    }

    @Test
    void transformDropsSchemaAttributesFromRoot(@TempDir Path tempDir) throws Exception {
        Element root = transformToRoot(TestFiles.SAMPLE, tempDir);

        assertEquals(0, root.getAttributes().getLength());
    }

    @Test
    void transformGroupsNonAdjacentMedicinesTogether(@TempDir Path tempDir) throws Exception {
        Path input = Files.writeString(tempDir.resolve("input.xml"), THREE_MEDICINES);

        Element root = transformToRoot(input, tempDir);
        List<Element> groups = childElements(root);
        List<Element> vitamins = childElements(groups.get(1));

        assertEquals(List.of("antibiotic", "vitamin"), tagNames(groups));
        assertEquals("MED-001", vitamins.get(0).getAttribute("id"));
        assertEquals("MED-003", vitamins.get(1).getAttribute("id"));
    }

    @Test
    void transformOfEmptyListProducesEmptyRoot(@TempDir Path tempDir) throws Exception {
        Path input = Files.writeString(tempDir.resolve("input.xml"), "<Medicines/>");

        Element root = transformToRoot(input, tempDir);

        assertEquals("Medicines", root.getTagName());
        assertTrue(childElements(root).isEmpty());
    }

    @Test
    void transformCreatesMissingOutputDirectories(@TempDir Path tempDir) {
        Path output = tempDir.resolve("nested").resolve("deeper").resolve("result.xml");

        transformer.transform(TestFiles.SAMPLE, output);

        assertTrue(Files.exists(output));
    }

    @Test
    void transformRejectsMalformedInput(@TempDir Path tempDir) {
        Path input = TestFiles.fixture("malformed.xml");
        Path output = tempDir.resolve("result.xml");

        XmlTransformException exception = assertThrows(XmlTransformException.class,
                () -> transformer.transform(input, output));

        assertTrue(exception.getMessage().startsWith("Cannot transform XML file: "));
        assertFalse(Files.exists(output));
    }

    @Test
    void transformRejectsMissingInput(@TempDir Path tempDir) {
        Path input = TestFiles.fixture("missing.xml");
        Path output = tempDir.resolve("result.xml");

        XmlTransformException exception = assertThrows(XmlTransformException.class,
                () -> transformer.transform(input, output));

        assertTrue(exception.getMessage().startsWith("Cannot transform XML file: "));
        assertFalse(Files.exists(output));
    }

    @Test
    void transformRejectsOutputInsideRegularFile(@TempDir Path tempDir) throws Exception {
        Path regularFile = Files.createFile(tempDir.resolve("file.txt"));
        Path output = regularFile.resolve("result.xml");

        XmlTransformException exception = assertThrows(XmlTransformException.class,
                () -> transformer.transform(TestFiles.SAMPLE, output));

        assertTrue(exception.getMessage().startsWith("Cannot write output file: "));
    }

    @Test
    void transformRejectsNullXmlFile(@TempDir Path tempDir) {
        Path output = tempDir.resolve("result.xml");

        assertThrows(IllegalArgumentException.class, () -> transformer.transform(null, output));
    }

    @Test
    void transformRejectsNullOutputFile() {
        assertThrows(IllegalArgumentException.class,
                () -> transformer.transform(TestFiles.SAMPLE, null));
    }

    @Test
    void constructorRejectsNullStylesheet() {
        assertThrows(IllegalArgumentException.class, () -> new XslTransformer(null));
    }

    @Test
    void constructorRejectsMissingStylesheet() {
        Path missing = Path.of("data", "missing.xsl");

        XmlTransformException exception = assertThrows(XmlTransformException.class,
                () -> new XslTransformer(missing));

        assertTrue(exception.getMessage().startsWith("Invalid XSL stylesheet: "));
    }

    @Test
    void constructorRejectsFileThatIsNotStylesheet() {
        Path notStylesheet = TestFiles.fixture("malformed.xml");

        assertThrows(XmlTransformException.class, () -> new XslTransformer(notStylesheet));
    }

    private Element transformToRoot(Path input, Path outputDirectory) throws Exception {
        Path output = outputDirectory.resolve("result.xml");
        transformer.transform(input, output);
        return read(output).getDocumentElement();
    }

    private static Document read(Path file) throws Exception {
        return XmlFactories.documentBuilderFactory().newDocumentBuilder().parse(file.toFile());
    }

    private static List<Element> childElements(Element parent) {
        List<Element> elements = new ArrayList<>();
        for (Node node = parent.getFirstChild(); node != null; node = node.getNextSibling()) {
            if (node instanceof Element element) {
                elements.add(element);
            }
        }
        return elements;
    }

    private static List<String> tagNames(List<Element> elements) {
        return elements.stream().map(Element::getTagName).toList();
    }

    private static String childText(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).item(0).getTextContent();
    }

    private static int count(Element parent, String tagName) {
        return parent.getElementsByTagName(tagName).getLength();
    }
}