package application;

import domain.PricePoint;
import infrastructure.api.AwattarClient;
import infrastructure.api.AwattarPriceDto;
import infrastructure.json.GsonPriceMapper;
import util.PriceNormalizer;
import util.TimeRangeFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.time.ZonedDateTime;

/**
 * Service layer responsible for orchestrating
 * data retrieval, mapping, and normalization.
 */
public class PriceService {

    private final AwattarClient apiClient;
    private final GsonPriceMapper mapper;
    private final PriceNormalizer normalizer;

    public PriceService(
            AwattarClient apiClient,
            GsonPriceMapper mapper,
            PriceNormalizer normalizer
    ) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.normalizer = normalizer;
    }

    /**
     * Loads normalized hourly prices for a given date.
     *
     * @param date target date
     * @param zone time zone
     * @return map of hourly prices
     */
    public Map<ZonedDateTime, Double> loadNormalizedPrices(
            LocalDate date,
            ZoneId zone
    ) {
        long[] timestamps = TimeRangeFactory.forThreeDays(date, zone);

        // 1. Fetch API DTOs
        List<AwattarPriceDto> dtoList =
                apiClient.fetchPrices(timestamps[0], timestamps[1], "at");

        // 2. Map to domain
        List<PricePoint> prices = mapper.toDomain(dtoList, zone);

        // 3. Normalize
        return normalizer.normalize(prices, date, zone);
    }
}
