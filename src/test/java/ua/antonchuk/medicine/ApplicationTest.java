package ua.antonchuk.medicine;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.config.AppConfig;
import ua.antonchuk.medicine.model.Manufacturer;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.model.MedicineForm;
import ua.antonchuk.medicine.model.MedicineGroup;
import ua.antonchuk.medicine.model.MedicinePackage;
import ua.antonchuk.medicine.model.PackageKind;
import ua.antonchuk.medicine.model.TestData;
import ua.antonchuk.medicine.model.Version;
import ua.antonchuk.medicine.parser.MedicineParseException;
import ua.antonchuk.medicine.parser.MedicineParser;
import ua.antonchuk.medicine.transform.XmlTransformException;
import ua.antonchuk.medicine.transform.XslTransformer;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    private static final Path XML = Path.of("data", "medicines.xml");
    private static final Path SCHEMA = Path.of("data", "medicines.xsd");
    private static final Path STYLESHEET = Path.of("data", "medicines-by-group.xsl");
    private static final Path OUTPUT = Path.of("output", "result.xml");

    private final AppConfig config = new AppConfig(XML, SCHEMA, STYLESHEET, OUTPUT);

    @Mock
    private MedicineParser firstParser;

    @Mock
    private MedicineParser secondParser;

    @Mock
    private XslTransformer transformer;

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(Application.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    private Application application() {
        return new Application(config, List.of(firstParser, secondParser), transformer);
    }

    private static Medicine medicine(String name, MedicineGroup group, String price) {
        MedicinePackage medicinePackage =
                new MedicinePackage(PackageKind.BLISTER, 10, new BigDecimal(price));
        Manufacturer manufacturer = new Manufacturer("Farmak", TestData.certificate(),
                medicinePackage, TestData.dosage());
        Version version = new Version(MedicineForm.TABLET, List.of(manufacturer));
        return new Medicine("MED-001", name, "Pharm", group, List.of("Generic"), List.of(version));
    }

    @Test
    void runParsesXmlFileWithEveryParser() {
        when(firstParser.parse(XML))
                .thenReturn(List.of(medicine("Alpha", MedicineGroup.VITAMIN, "30")));
        when(secondParser.parse(XML)).thenReturn(List.of());

        application().run();

        verify(firstParser).parse(XML);
        verify(secondParser).parse(XML);
    }

    @Test
    void runTransformsXmlFileIntoConfiguredOutput() {
        application().run();

        verify(transformer).transform(XML, OUTPUT);
    }

    @Test
    void runLogsMedicinesSortedByEveryComparator() {
        when(firstParser.parse(XML)).thenReturn(List.of(
                medicine("Charlie", MedicineGroup.ANALGESIC, "10"),
                medicine("Alpha", MedicineGroup.VITAMIN, "30"),
                medicine("Bravo", MedicineGroup.ANTIBIOTIC, "50")));

        application().run();

        List<String> messages = appender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .filter(message -> message.startsWith("Sorted"))
                .toList();
        assertEquals(List.of(
                "Sorted by name: Alpha, Bravo, Charlie",
                "Sorted by group and name: Bravo, Charlie, Alpha",
                "Sorted by cheapest price: Charlie, Alpha, Bravo",
                "Sorted by name: ",
                "Sorted by group and name: ",
                "Sorted by cheapest price: "), messages);
    }

    @Test
    void runStopsWhenParserFails() {
        when(firstParser.parse(XML)).thenThrow(new MedicineParseException("boom"));
        Application application = application();

        assertThrows(MedicineParseException.class, application::run);

        verify(secondParser, never()).parse(any());
        verifyNoInteractions(transformer);
    }

    @Test
    void runPropagatesTransformFailure() {
        doThrow(new XmlTransformException("boom")).when(transformer).transform(XML, OUTPUT);
        Application application = application();

        assertThrows(XmlTransformException.class, application::run);
    }

    @Test
    void constructorRejectsNullConfig() {
        List<MedicineParser> parsers = List.of(firstParser);

        assertThrows(IllegalArgumentException.class,
                () -> new Application(null, parsers, transformer));
    }

    @Test
    void constructorRejectsNullParsers() {
        assertThrows(IllegalArgumentException.class,
                () -> new Application(config, null, transformer));
    }

    @Test
    void constructorRejectsEmptyParsers() {
        List<MedicineParser> parsers = List.of();

        assertThrows(IllegalArgumentException.class,
                () -> new Application(config, parsers, transformer));
    }

    @Test
    void constructorRejectsNullTransformer() {
        List<MedicineParser> parsers = List.of(firstParser);

        assertThrows(IllegalArgumentException.class,
                () -> new Application(config, parsers, null));
    }

    @Test
    void createBuildsApplicationThatProducesOutputFile(@TempDir Path tempDir) {
        Path output = tempDir.resolve("result.xml");
        AppConfig realConfig = new AppConfig(XML, SCHEMA, STYLESHEET, output);

        Application.create(realConfig).run();

        assertTrue(Files.exists(output));
    }

    @Test
    void createRejectsNullConfig() {
        assertThrows(IllegalArgumentException.class, () -> Application.create(null));
    }
}