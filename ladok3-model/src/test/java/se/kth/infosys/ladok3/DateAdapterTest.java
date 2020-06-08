package se.kth.infosys.ladok3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class DateAdapterTest {

  private static final ZoneId ZONE_ID = ZoneId.of("Europe/Stockholm");

  @Test
  public void testParseDate() {
    final Date result = DateAdapter.parseDate("2020-05-08");
    assertThat(result).isEqualTo("2020-05-008T00:00:00.000");
  }

  @Test
  public void testParseDateTime() {
    final Date result = DateAdapter.parseDateTime("2020-05-08T00:00:00.000");
    assertThat(result).isEqualTo("2020-05-008T00:00:00.000");
  }


  @Test
  public void testPrintDate() {
    final LocalDate localDate = LocalDate.of(2020, 5, 8);
    final String s = DateAdapter.printDate(Date.from(localDate.atStartOfDay(ZONE_ID).toInstant()));
    assertThat(s).isEqualTo("2020-05-08");
  }

  @Test
  public void testPrintDateTime() {
    final LocalDateTime localDateTime = LocalDateTime.of(2020, 5, 8, 0, 0, 0);
    final String s = DateAdapter.printDateTime(Date.from(localDateTime.atZone(ZONE_ID).toInstant()));
    assertThat(s).isEqualTo("2020-05-08T00:00:00.000");
  }

  @Test
  public void testParseFail() {
    assertThrows(DateTimeParseException.class, () -> DateAdapter.parseDateTime("2020-01-15T00"));
  }
}
