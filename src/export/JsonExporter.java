package export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JsonExporter {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");


    public static JsonObject toNormalizedJson(Map<ZonedDateTime, Double> normalized) {
        JsonObject root = new JsonObject();
        root.addProperty("timezone", "Europe/Vienna");
        root.addProperty("unit", "ct/kWh");

        JsonArray dataArray = new JsonArray();

        for (Map.Entry<ZonedDateTime, Double> entry : normalized.entrySet()) {
            JsonObject dataPoint = new JsonObject();
            ZonedDateTime start = entry.getKey();
            ZonedDateTime end = start.plusHours(1);

            dataPoint.addProperty("start", start.format(FORMATTER));
            dataPoint.addProperty("end", end.format(FORMATTER));

            Double price = entry.getValue();
            if (price.isNaN()) {
                dataPoint.addProperty("price", "NaN");
            } else {
                dataPoint.addProperty("price", price);
            }

            dataArray.add(dataPoint);
        }

        root.add("data", dataArray);
        return root;
    }


    public static String toNormalizedJsonString(Map<ZonedDateTime, Double> normalized) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(toNormalizedJson(normalized));
    }
}
