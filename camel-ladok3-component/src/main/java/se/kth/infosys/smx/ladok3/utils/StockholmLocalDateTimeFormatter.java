package se.kth.infosys.smx.ladok3.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility for creating date time strings in Stockholm local time from java.util.Date.
 * java.util.Date carries no timezone and are simply adjusted to the default timezone of the JVM when parsed
 * from UTC strings such as 2017-09-18T06:22:44.299Z. This utility accounts for the default timezone to produce a
 * formatted string in the time zone of Stockholm.
 */
public final class StockholmLocalDateTimeFormatter {
    private static final ZoneId STOCKHOLM_ZONE = ZoneId.of("Europe/Stockholm");
    private static final ZoneId SYSTEM_ZONE = ZoneId.systemDefault();

    public static String formatAsStockolmLocalDateTime(Date date) {
        return formatAsStockolmLocalDateTime(date, SYSTEM_ZONE);
    }

    static String formatAsStockolmLocalDateTime(Date date, ZoneId timezoneOfParser) {
        return ZonedDateTime.ofInstant(date.toInstant(), timezoneOfParser)
                .withZoneSameInstant(STOCKHOLM_ZONE)
                .toLocalDateTime()
                .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME); //ex: '2011-12-03T10:15:30'
    }

    /**
     * Parse date string as formatted by formatAsStockolmLocalDateTime into Date.
     * @param dateStr date string as formatted by formatAsStockolmLocalDateTime 
     * @return corresponding Date instance.
     */
    public static Date parse(String dateStr) {
        return Date.from(LocalDateTime.parse(dateStr).atZone(STOCKHOLM_ZONE).toInstant());
    }
}
