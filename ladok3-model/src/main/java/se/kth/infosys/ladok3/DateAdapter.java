package se.kth.infosys.ladok3;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.xml.bind.DatatypeConverter;

/**
 * A class that is used by JAXB when it converts an XML date into a Java type.
 * This implementation converts to a <code>java.util.Date</code>.
 * <p>
 * Read more here:
 * <a href="https://jaxb.java.net/guide/Using_different_datatypes.html">https://jaxb.java.net/guide/Using_different_datatypes.html</a>
 */
public class DateAdapter {
  // The use of SimpleDateFormat is because L3 expects the date to be passed without any timezone reference (black box tested)
  // Previously this was solved by suppressing the TZ from XMLGregorianCalendar but now needs to be handled by this adapter
  private static final SimpleDateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
  private static final SimpleDateFormat formatterDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

  public static Date parseDate(final String date) {
    return DatatypeConverter.parseDate(date).getTime();
  }

  public static String printDate(final Date date) {
    return formatterDate.format(date);
  }

  public static Date parseDateTime(final String dateTime) {
    return DatatypeConverter.parseDateTime(dateTime).getTime();
  }

  public static String printDateTime(final Date dateTime) {
    return formatterDateTime.format(dateTime);
  }
}