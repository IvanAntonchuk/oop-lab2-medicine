package ua.antonchuk.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.config.AppConfig;

import java.nio.file.Path;

public final class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    private static final Path DEFAULT_CONFIG_FILE = Path.of("config", "app.properties");

    private Main() {
    }

    public static void main(String[] args) {
        Path configFile = args.length > 0 ? Path.of(args[0]) : DEFAULT_CONFIG_FILE;
        LOGGER.info("Medicine application started");
        try {
            Application.create(AppConfig.load(configFile)).run();
        } catch (RuntimeException e) {
            LOGGER.error("Medicine application failed: {}", e.getMessage());
            throw e;
        }
        LOGGER.info("Medicine application finished");
    }
}