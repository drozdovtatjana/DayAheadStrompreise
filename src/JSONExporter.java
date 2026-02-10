import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class JSONExporter {

    /**
     * Converts a normalized hourly map into a JSON object.
     * NaN values become the string "NaN".
     */
    public static JsonObject toNormalizedJson(Map<ZonedDateTime, Double> normalized) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mmXXX");

        JsonObject root = new JsonObject();
        root.addProperty("timezone", "Europe/Vienna");
        root.addProperty("unit", "ct/kWh");

        JsonArray dataArray = new JsonArray();

        for (Map.Entry<ZonedDateTime, Double> entry : normalized.entrySet()) {
            JsonObject dataPoint = new JsonObject();
            ZonedDateTime start = entry.getKey();
            ZonedDateTime end = start.plusHours(1);

            dataPoint.addProperty("start", start.format(formatter));
            dataPoint.addProperty("end", end.format(formatter));

            Double price = entry.getValue();
            if (price.isNaN()) {
                dataPoint.addProperty("price", "NaN"); // <-- literal "NaN" as string
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
