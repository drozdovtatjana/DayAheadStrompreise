package util;

import domain.PricePoint;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class PriceNormalizer {

    public Map<ZonedDateTime, Double> normalize(
            List<PricePoint> prices,
            LocalDate selectedDate,
            ZoneId zone
    ) {
        Map<ZonedDateTime, Double> normalized = new LinkedHashMap<>();

        ZonedDateTime start =
                selectedDate.minusDays(1).atStartOfDay(zone);
        ZonedDateTime end =
                selectedDate.plusDays(2).atStartOfDay(zone);

        // Build full hourly grid (DST-safe)
        for (ZonedDateTime t = start; t.isBefore(end); t = t.plusHours(1)) {
            normalized.put(t, Double.NaN);
        }

        // Fill known prices
        for (PricePoint p : prices) {
            ZonedDateTime hour = p.getStart()
                    .withMinute(0)
                    .withSecond(0)
                    .withNano(0);

            if (normalized.containsKey(hour)) {
                normalized.put(hour, p.getPriceCtPerKwh());
            }
        }

        return normalized;
    }
}