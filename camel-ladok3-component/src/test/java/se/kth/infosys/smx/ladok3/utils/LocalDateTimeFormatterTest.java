package se.kth.infosys.smx.ladok3.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class LocalDateTimeFormatterTest {

    private static final String PARSER_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    @Test
    public void testTimeZoneIndependency() throws ParseException {
        for (String zoneId : ZoneId.getAvailableZoneIds()) {
            ZoneId defaultZone = ZoneId.of(zoneId);
            SimpleDateFormat dateFormat = new SimpleDateFormat(PARSER_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone(defaultZone));

            Date parsedDate = dateFormat.parse("2017-09-18T06:22:44.299Z");
            String expectedDateTimeString = "2017-09-18T08:22:44.299"; // Stockholm is 2 hours before UTC at this date

            String actualDateTimeString = LocalDateTimeFormatter.formatAsStockolmLocalDateTime(parsedDate, defaultZone);
            assertEquals("Result for zone " + zoneId, expectedDateTimeString, actualDateTimeString);
        }
    }

    @Test
    public void testCorrectLocalDate() throws ParseException {
        ZoneId defaultZone = ZoneId.of("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat(PARSER_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(defaultZone));

        Date parsedDate = dateFormat.parse("2017-09-17T23:22:44.299Z");
        String expectedDateTimeString = "2017-09-18T01:22:44.299"; // Stockholm is 2 hours before UTC at this date
        String actualDateTimeString = LocalDateTimeFormatter.formatAsStockolmLocalDateTime(parsedDate, defaultZone);

        assertEquals(expectedDateTimeString, actualDateTimeString);
    }

    @Test
    public void testWinterLocalDate() throws ParseException {
        ZoneId defaultZone = ZoneId.of("UTC");
        SimpleDateFormat dateFormat = new SimpleDateFormat(PARSER_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone(defaultZone));

        Date parsedDate = dateFormat.parse("2017-01-17T12:22:44.299Z");
        String expectedDateTimeString = "2017-01-17T13:22:44.299"; // Stockholm is 1 hour before UTC at this date
        String actualDateTimeString = LocalDateTimeFormatter.formatAsStockolmLocalDateTime(parsedDate, defaultZone);

        assertEquals(expectedDateTimeString, actualDateTimeString);
    }
}