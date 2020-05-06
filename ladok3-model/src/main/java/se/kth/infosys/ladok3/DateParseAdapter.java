package se.kth.infosys.ladok3;

import static java.time.ZoneOffset.UTC;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

/**
 * A class that is used by JAXB when it converts an XML date into a Java type.
 * This implementation converts to a <code>java.util.Date</code>.
 * <p>
 * Read more here:
 * <a href="https://jaxb.java.net/guide/Using_different_datatypes.html">https://jaxb.java.net/guide/Using_different_datatypes.html</a>
 */
public class DateParseAdapter {

  private static final DateTimeFormatter formatterDateTimeZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]Z");
  private static final DateTimeFormatter formatterDateTime = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]");
  private static final DateTimeFormatter formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  public static Date parse(final String date) {
    try {
      return parseDateTimeZone(date);
    } catch (DateTimeParseException e) {
      try {
        return parseDateTime(date);
      } catch (DateTimeParseException dateTimeParseException) {
        return parseDate(date);
      }
    }
  }

  public static String print(final Date date) {
    final UnsupportedOperationException e = new UnsupportedOperationException("DateParseAdapter only supports parsing");
    e.printStackTrace();
    throw e;
  }

  private static Date parseDateTimeZone(final String dateTime) throws DateTimeParseException {
    ZonedDateTime d = ZonedDateTime.parse(dateTime, formatterDateTimeZone);
    return Date.from(d.toInstant());
  }

  private static Date parseDateTime(final String dateTime) {
    LocalDateTime d = LocalDateTime.parse(dateTime, formatterDateTime);
    return Date.from(d.toInstant(UTC));

  }

  private static Date parseDate(final String date) {
    LocalDate d = LocalDate.parse(date, formatterDate);
    return Date.from(d.atTime(0,0).toInstant(UTC));
  }
}
