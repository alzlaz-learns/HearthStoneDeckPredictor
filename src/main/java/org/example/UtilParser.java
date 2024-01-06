package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UtilParser {

    private static DateTimeFormatter HSLogFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    public static LocalDateTime parseDateTime(String folderName) {
        try {
            // Assuming the format "Hearthstone_YYYY_MM_DD_hh_mm_ss"
            String dateTimePart = folderName.substring("Hearthstone_".length());
            return LocalDateTime.parse(dateTimePart, HSLogFormatter);
        } catch (DateTimeParseException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
            return null;
        }
    }
}
