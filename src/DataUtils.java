import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

public class DataUtils {

    public static Map<ZonedDateTime, Double> normalizeData(List<APIData> data, ZoneId zone) {
        Map<ZonedDateTime, Double> hourlyPrices = new LinkedHashMap<>();

        if (data.isEmpty()) return hourlyPrices;

        // Determine range: min start to max end
        Date minDate = data.stream().map(APIData::getStart_timestamp).min(Date::compareTo).get();
        Date maxDate = data.stream().map(APIData::getEnd_timestamp).max(Date::compareTo).get();

        ZonedDateTime start = ZonedDateTime.ofInstant(minDate.toInstant(), zone)
                .withMinute(0).withSecond(0).withNano(0);
        ZonedDateTime end = ZonedDateTime.ofInstant(maxDate.toInstant(), zone)
                .withMinute(0).withSecond(0).withNano(0);

        // Create hourly slots and initialize with NaN
        ZonedDateTime current = start;
        while (!current.isAfter(end)) {
            hourlyPrices.put(current, Double.NaN);
            current = current.plusHours(1);
        }

        // Fill available data
        for (APIData d : data) {
            ZonedDateTime ts = ZonedDateTime.ofInstant(d.getStart_timestamp().toInstant(), zone);
            hourlyPrices.put(ts, d.getMarketprice());
        }

        return hourlyPrices;
    }
}
