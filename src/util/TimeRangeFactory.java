package util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Factory to generate time ranges for API requests.
 */
public class TimeRangeFactory {

    /**
     * Returns Unix timestamps (ms) covering:
     * previous day, selected day, next day
     *
     * @param date selected date
     * @param zone time zone
     * @return long[]{startMs, endMs}
     */
    public static long[] forThreeDays(LocalDate date, ZoneId zone) {
        LocalDate previousDay = date.minusDays(1);
        LocalDate nextDay = date.plusDays(2);

        ZonedDateTime start = previousDay.atStartOfDay(zone);
        ZonedDateTime end = nextDay.atStartOfDay(zone);

        return new long[]{start.toInstant().toEpochMilli(), end.toInstant().toEpochMilli()};
    }
}
