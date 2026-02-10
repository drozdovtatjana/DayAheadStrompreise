import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class DataUtils {


        /**
         * Normalizes aWATTar market data into a complete hourly grid
         * for the previous day, selected day and next day.
         *
         * Missing hours are explicitly represented as NaN.
         * Time zone: Europe/Vienna (DST-aware).
         */
        public static Map<ZonedDateTime, Double> normalizeData(
                List<APIData> apiData,
                LocalDate selectedDate,
                ZoneId zone
        ) {
            Map<ZonedDateTime, Double> normalized = new LinkedHashMap<>();

            // 1Define fixed calendar range (not data-driven!)
            ZonedDateTime start =
                    selectedDate.minusDays(1).atStartOfDay(zone);
            ZonedDateTime end =
                    selectedDate.plusDays(2).atStartOfDay(zone);

            //  Build full hourly raster (DST-safe)
            ZonedDateTime cursor = start;
            while (cursor.isBefore(end)) {
                normalized.put(cursor, Double.NaN);
                cursor = cursor.plusHours(1);
            }

            // 3 Fill available API data
            for (APIData d : apiData) {
                ZonedDateTime hour =
                        ZonedDateTime.ofInstant(
                                d.getStart_timestamp().toInstant(),
                                zone
                        ).withMinute(0).withSecond(0).withNano(0);

                // EUR/MWh → ct/kWh
                double priceCtKwh = d.getMarketprice() / 10.0;

                if (normalized.containsKey(hour)) {
                    normalized.put(hour, priceCtKwh);
                }
            }

            return normalized;
        }
    }


