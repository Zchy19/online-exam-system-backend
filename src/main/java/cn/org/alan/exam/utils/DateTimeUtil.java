package cn.org.alan.exam.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class DateTimeUtil {

    private DateTimeUtil() {
    }
    private static String dataFormat = "yyyy-MM-dd";
    private static String format = "yyyy-MM-dd HH:mm:ss";

    
    public static LocalDateTime getDateTime() {
        return LocalDateTime.parse(datetimeToStr(LocalDateTime.now()), DateTimeFormatter.ofPattern(format));
    }
    public static LocalDate getDate() {
        return LocalDate.parse(dateToStr(LocalDate.now()), DateTimeFormatter.ofPattern(dataFormat));
    }

    
    public static String datetimeToStr(LocalDateTime dateTime) {
        return DateTimeFormatter.ofPattern(format).format(dateTime);

    }
    public static String dateToStr(LocalDate date) {
        return DateTimeFormatter.ofPattern(dataFormat).format(date);

    }

}
