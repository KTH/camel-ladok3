package se.kth.infosys.ladok3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.format.DateTimeParseException;
import java.util.Date;
import org.junit.jupiter.api.Test;

public class DateParseAdapterTest {

  @Test
  public void testParseDate() {
    final Date result = DateParseAdapter.parse("2020-01-15");
    assertThat(result).isEqualTo("2020-01-15T00:00:00.000+0000");
  }

  @Test
  public void testParseDateTime() {
    final Date result = DateParseAdapter.parse("2020-01-15T00:00:00");
    assertThat(result).isEqualTo("2020-01-15T00:00:00.000+0000");
  }

  @Test
  public void testParseDateTimeMs() {
    final Date result = DateParseAdapter.parse("2020-01-15T00:00:00.000");
    assertThat(result).isEqualTo("2020-01-15T00:00:00.000+0000");
  }

  @Test
  public void testParseDateTimeZone() {
    final Date result = DateParseAdapter.parse("2020-01-15T00:00:00+0000");
    assertThat(result).isEqualTo("2020-01-15T00:00:00.000+0000");
  }

  @Test
  public void testParseDateTimeMsZone() {
    final Date result = DateParseAdapter.parse("2020-01-15T00:00:00.000+0000");
    assertThat(result).isEqualTo("2020-01-15T00:00:00.000+0000");
  }

  @Test
  public void testParseFail() {
    assertThrows(DateTimeParseException.class, () -> DateParseAdapter.parse("2020-01-15T00"));
  }

  @Test
  public void testPrint() {
    assertThrows(UnsupportedOperationException.class, () -> DateParseAdapter.print(new Date()));
  }
}
