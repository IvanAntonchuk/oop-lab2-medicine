package ua.antonchuk.medicine.parser;

import ua.antonchuk.medicine.model.Medicine;

import java.nio.file.Path;
import java.util.List;

public interface MedicineParser {

    List<Medicine> parse(Path xmlFile);
}