package ua.antonchuk.medicine;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.config.ConfigException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(Main.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(appender);
        appender.stop();
    }

    private List<String> messages() {
        return appender.list.stream().map(ILoggingEvent::getFormattedMessage).toList();
    }

    @Test
    void mainRunsApplicationWithDefaultConfigFile() {
        Main.main(new String[0]);

        assertEquals(List.of("Medicine application started", "Medicine application finished"),
                messages());
        assertTrue(Files.exists(Path.of("output", "medicines-by-group.xml")));
    }

    @Test
    void mainUsesConfigFileFromArguments(@TempDir Path tempDir) throws IOException {
        Path output = tempDir.resolve("result.xml");
        Path config = tempDir.resolve("app.properties");
        Files.writeString(config, """
                xml.path=data/medicines.xml
                xsd.path=data/medicines.xsd
                xsl.path=data/medicines-by-group.xsl
                output.path=%s
                """.formatted(output.toString().replace('\\', '/')));

        Main.main(new String[] {config.toString()});

        assertTrue(Files.exists(output));
    }

    @Test
    void mainLogsErrorAndRethrowsWhenConfigIsMissing(@TempDir Path tempDir) {
        String missing = tempDir.resolve("missing.properties").toString();

        assertThrows(ConfigException.class, () -> Main.main(new String[] {missing}));

        ILoggingEvent last = appender.list.get(appender.list.size() - 1);
        assertEquals(Level.ERROR, last.getLevel());
        assertTrue(last.getFormattedMessage().startsWith("Medicine application failed: "));
    }
}