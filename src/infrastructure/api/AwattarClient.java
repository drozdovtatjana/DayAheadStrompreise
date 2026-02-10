package infrastructure.api;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Client responsible for communicating with the aWATTar API.
 */
public class AwattarClient {

    private static final String BASE_URL =
            "https://api.awattar.%s/v1/marketdata?start=%d&end=%d";

    private final HttpClient httpClient;
    private final Gson gson;

    public AwattarClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Fetches raw price data from the aWATTar API.
     *
     * @param start Unix timestamp in milliseconds
     * @param end   Unix timestamp in milliseconds
     * @param locale API locale (e.g. "at")
     * @return list of price DTOs
     */
    public List<AwattarPriceDto> fetchPrices(
            long start,
            long end,
            String locale
    ) {
        String url = String.format(BASE_URL, locale, start, end);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException(
                        "API error: HTTP " + response.statusCode()
                );
            }

            AwattarResponseDto dto =
                    gson.fromJson(response.body(), AwattarResponseDto.class);

            return dto.data;

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to fetch data from aWATTar API", e);
        }
    }
}
