package ua.edu.dnu.warehouse.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FormatUtil {
    public static final  DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
    public static final DateTimeFormatter DAY_MONTH_FORMATTER =
            DateTimeFormatter.ofPattern("dd-MM");
    public static String formatDateTime(LocalDateTime dateTime){
        return dateTime.format(DATE_TIME_FORMATTER);
    }

    public static String formatDayMonth(LocalDateTime dateTime){
        return dateTime.format(DAY_MONTH_FORMATTER);
    }

}
