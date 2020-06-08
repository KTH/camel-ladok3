package se.kth.infosys.ladok3;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * A class that is used by JAXB when it converts an XML date into a Java type.
 * This implementation converts to a <code>java.util.Date</code>.
 * <p>
 * Read more here:
 * <a href="https://jaxb.java.net/guide/Using_different_datatypes.html">https://jaxb.java.net/guide/Using_different_datatypes.html</a>
 */
public class DateAdapter {

  private static final ZoneId ZONE_ID = ZoneId.of("Europe/Stockholm");

  private static final DateTimeFormatter formatterDateTime =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS").withZone(ZONE_ID);

  private static final DateTimeFormatter formatterDate =
      DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZONE_ID);

  public static String printDateTime(final Date date) {
    return LocalDateTime.ofInstant(date.toInstant(), ZONE_ID).format(formatterDateTime);
  }

  public static String printDate(final Date date) {
    return LocalDate.ofInstant(date.toInstant(), ZONE_ID).format(formatterDate);
  }

  public static Date parseDateTime(final String dateTime) {
    LocalDateTime localDateTime = LocalDateTime.parse(dateTime, formatterDateTime);
    return Date.from(localDateTime.atZone(ZONE_ID).toInstant());
  }

  public static Date parseDate(final String date) {
    LocalDate d = LocalDate.parse(date, formatterDate);
    return Date.from(d.atTime(0,0).atZone(ZONE_ID).toInstant());
  }
}
