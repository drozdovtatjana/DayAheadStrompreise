import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class APIfunction {
    public String FETCING(long start, long end) {
        String locale ="at";
        String url = buildUrl( start, end,locale);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response;

        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            return null;
        }

        System.out.println("Status Code: " + response.statusCode());

        return response.body();
    }

    public static String buildUrl(long start, long end, String locale) {
        return String.format("https://api.awattar.%s/v1/marketdata?start=%d&end=%d", locale,start, end);
    }
}
