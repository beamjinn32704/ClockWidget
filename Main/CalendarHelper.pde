
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public static class CalendarHelper {

  private static Instant instant = Instant.now();

  /**
   * Get the day of the week.
   * @return day of week as an integer.
   */
  public static int getDayOfTheWeek() {
    resetInstant();
    int weekday = instant.atZone(ZoneOffset.systemDefault()).getDayOfWeek().getValue();
    return weekday;
  }

  public static String timeSpecFormat() {
    resetInstant();
    String specFormat = "";
    ZonedDateTime zone = instant.atZone(ZoneOffset.systemDefault());
    specFormat += zone.getHour();
    specFormat += ":" + zone.getMinute();
    specFormat += ":" + zone.getSecond();
    return specFormat;
  }

  public static String dayOfYearFormat() {
    resetInstant();
    String format = "";
    ZonedDateTime zone = instant.atZone(ZoneOffset.systemDefault());
    format += zone.getMonthValue() + "/";
    format += zone.getDayOfMonth() + "/";
    format += zone.getYear();
    return format;
  }

  /**
   * Resets the instant object.
   */
  private static void resetInstant() {
    instant = Instant.now();
  }

  /**
   * Get the instant object.
   * @return instant object
   */
  public static Instant getInstant() {
    resetInstant();
    return instant;
  }

  public static int timeToNextSec() {
    long now = Date.from(getInstant()).getTime();
    long timeTill = now % 1000;
    return (int) timeTill;
  }
}
