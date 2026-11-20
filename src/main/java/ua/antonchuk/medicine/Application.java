package ua.antonchuk.medicine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.antonchuk.medicine.comparator.MedicineComparators;
import ua.antonchuk.medicine.config.AppConfig;
import ua.antonchuk.medicine.model.Medicine;
import ua.antonchuk.medicine.parser.MedicineParser;
import ua.antonchuk.medicine.parser.XmlValidator;
import ua.antonchuk.medicine.parser.dom.DomMedicineParser;
import ua.antonchuk.medicine.parser.sax.SaxMedicineParser;
import ua.antonchuk.medicine.parser.stax.StaxMedicineParser;
import ua.antonchuk.medicine.transform.XslTransformer;
import ua.antonchuk.medicine.util.Validation;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    private static final Map<String, Comparator<Medicine>> SORTINGS = createSortings();

    private final AppConfig config;
    private final List<MedicineParser> parsers;
    private final XslTransformer transformer;

    public Application(AppConfig config, List<MedicineParser> parsers,
                       XslTransformer transformer) {
        this.config = Validation.requireNonNull(config, "Config");
        this.parsers = Validation.requireNonEmptyList(parsers, "Parsers");
        this.transformer = Validation.requireNonNull(transformer, "Transformer");
    }

    public static Application create(AppConfig config) {
        Validation.requireNonNull(config, "Config");
        XmlValidator validator = new XmlValidator(config.getSchemaFile());
        List<MedicineParser> parsers = List.of(
                new SaxMedicineParser(validator),
                new DomMedicineParser(validator),
                new StaxMedicineParser(validator));
        return new Application(config, parsers, new XslTransformer(config.getStylesheetFile()));
    }

    public void run() {
        for (MedicineParser parser : parsers) {
            logSorted(parser.parse(config.getXmlFile()));
        }
        transformer.transform(config.getXmlFile(), config.getOutputFile());
    }

    private static void logSorted(List<Medicine> medicines) {
        SORTINGS.forEach((title, comparator) -> LOGGER.info("Sorted {}: {}", title,
                medicines.stream()
                        .sorted(comparator)
                        .map(Medicine::getName)
                        .collect(Collectors.joining(", "))));
    }

    private static Map<String, Comparator<Medicine>> createSortings() {
        Map<String, Comparator<Medicine>> sortings = new LinkedHashMap<>();
        sortings.put("by name", MedicineComparators.BY_NAME);
        sortings.put("by group and name", MedicineComparators.BY_GROUP_THEN_NAME);
        sortings.put("by cheapest price", MedicineComparators.BY_CHEAPEST_PRICE);
        return sortings;
    }
}